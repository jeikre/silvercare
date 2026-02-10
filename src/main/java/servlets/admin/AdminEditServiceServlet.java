package servlets.admin;

import dbaccess.AdminDAO;
import models.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
@WebServlet("/admin/services/edit")
public class AdminEditServiceServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        int serviceId;
        try {
            serviceId = Integer.parseInt(request.getParameter("service_id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/services?err=missingId");
            return;
        }

        try {
            Service s = adminDAO.getServiceById(serviceId);
            if (s == null) {
                response.sendRedirect(request.getContextPath() + "/admin/services?err=notFound");
                return;
            }

            request.setAttribute("service", s);
            request.setAttribute("timeSlots", adminDAO.getTimeSlotsByServiceId(serviceId));

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("editError", "Unable to load service.");
        }

        request.getRequestDispatcher("/admin/editService.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            request.setCharacterEncoding("UTF-8");
            int serviceId = Integer.parseInt(request.getParameter("service_id"));

            // -----------------------
            // 1) Delete a timeslot
            // -----------------------
            String deleteSlotId = request.getParameter("delete_slot_id");
            if (deleteSlotId != null && !deleteSlotId.isBlank()) {
                adminDAO.deleteTimeSlot(Integer.parseInt(deleteSlotId));
                response.sendRedirect(request.getContextPath()
                        + "/admin/services/edit?service_id=" + serviceId + "&updated=1");
                return;
            }

            // -----------------------
            // 2) Add a timeslot (single or range)
            // -----------------------
            String addSlot = request.getParameter("add_slot");
            if ("1".equals(addSlot)) {
                String slotType = request.getParameter("slot_type"); // single / range
                String start = trimOrNull(request.getParameter("new_slot_start"));
                String end = trimOrNull(request.getParameter("new_slot_end"));

                if (start == null || start.isEmpty()) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin/services/edit?service_id=" + serviceId + "&err=slotAdd");
                    return;
                }

                String slotTime;
                if ("range".equals(slotType)) {
                    if (end == null || end.isEmpty()) {
                        response.sendRedirect(request.getContextPath()
                                + "/admin/services/edit?service_id=" + serviceId + "&err=slotAdd");
                        return;
                    }
                    slotTime = start + "-" + end;  // e.g. 1pm-6pm or 1-6
                } else {
                    slotTime = start;              // e.g. 6pm
                }

                adminDAO.addTimeSlot(serviceId, slotTime);
                response.sendRedirect(request.getContextPath()
                        + "/admin/services/edit?service_id=" + serviceId + "&updated=1");
                return;
            }

            // -----------------------
            // 3) Update service main fields
            // -----------------------
            Service s = new Service();
            s.setServiceId(serviceId);

            String cat = request.getParameter("category_id");
            if (cat == null || cat.trim().isEmpty()) s.setCategoryId(null);
            else s.setCategoryId(Integer.parseInt(cat));

            s.setServiceName(request.getParameter("service_name"));
            s.setServiceDescription(request.getParameter("service_description"));
            s.setPrice(new BigDecimal(request.getParameter("price")));
            s.setDuration(request.getParameter("duration"));

            // image handling
            String savedImagePath = saveUploadedServiceImage(request, "service_image_file");

            String existing = request.getParameter("existing_image");
            if (existing != null) existing = existing.trim();
            if (existing != null && existing.isEmpty()) existing = null;

            if (savedImagePath != null) s.setServiceImage(savedImagePath);
            else s.setServiceImage(existing);

            adminDAO.updateService(s);

            // -----------------------
            // 4) Bulk update timeslots text (existing)
            // -----------------------
            String[] slotIds = request.getParameterValues("slot_id");
            String[] slotTimes = request.getParameterValues("slot_time");

            if (slotIds != null && slotTimes != null && slotIds.length == slotTimes.length) {
                for (int i = 0; i < slotIds.length; i++) {
                    int slotId = Integer.parseInt(slotIds[i]);
                    String st = trimOrNull(slotTimes[i]);
                    if (st != null && !st.isEmpty()) {
                        adminDAO.updateTimeSlot(slotId, st);
                    }
                }
            }

            response.sendRedirect(request.getContextPath()
                    + "/admin/services/edit?service_id=" + serviceId + "&updated=1");

        } catch (Exception e) {
            e.printStackTrace();
            String sid = request.getParameter("service_id");
            if (sid == null) sid = "";
            response.sendRedirect(request.getContextPath()
                    + "/admin/services/edit?service_id=" + sid + "&err=edit");
        }
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private String saveUploadedServiceImage(HttpServletRequest request, String fieldName) throws Exception {

        Part filePart = request.getPart(fieldName);
        if (filePart == null || filePart.getSize() == 0) return null;

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return null;

        String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String ext = "";
        int dot = submitted.lastIndexOf('.');
        if (dot >= 0) ext = submitted.substring(dot).toLowerCase();

        if (!(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".webp"))) {
            return null;
        }

        String newName = "svc_" + UUID.randomUUID().toString().replace("-", "") + ext;

        String uploadDir = request.getServletContext().getRealPath("/uploads/services");
        if (uploadDir == null) return null;

        Path dirPath = Path.of(uploadDir);
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        Path savePath = dirPath.resolve(newName);
        filePart.write(savePath.toString());

        return "/uploads/services/" + newName;
    }
}
