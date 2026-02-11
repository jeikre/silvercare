package servlets.admin;

import dbaccess.AdminDAO;
import models.Service;
import models.TimeSlot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/services/edit")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
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

            List<TimeSlot> slots = adminDAO.getTimeSlotsByServiceId(serviceId);

            request.setAttribute("service", s);
            request.setAttribute("slots", slots);

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

            String action = request.getParameter("action");
            if (action == null) action = "save";

            int serviceId = Integer.parseInt(request.getParameter("service_id"));

            // ===== Add slot =====
            if ("addSlot".equalsIgnoreCase(action)) {

                String mode = request.getParameter("slot_mode");  // single / range
                String single = request.getParameter("single_time"); // HH:mm
                String start = request.getParameter("start_time");   // HH:mm
                String end = request.getParameter("end_time");       // HH:mm

                boolean ok = adminDAO.addTimeSlot(serviceId, mode, single, start, end);

                response.sendRedirect(request.getContextPath() + "/admin/services/edit?service_id=" + serviceId
                        + (ok ? "&slotAdded=1" : "&slotErr=1"));
                return;
            }

            // ===== Delete slot =====
            if ("deleteSlot".equalsIgnoreCase(action)) {
                int slotId = Integer.parseInt(request.getParameter("slot_id"));
                boolean ok = adminDAO.deleteTimeSlot(slotId, serviceId);

                response.sendRedirect(request.getContextPath() + "/admin/services/edit?service_id=" + serviceId
                        + (ok ? "&slotDeleted=1" : "&slotErr=1"));
                return;
            }

            // ===== Save service details =====
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

            response.sendRedirect(request.getContextPath() + "/admin/services?updated=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/services?err=edit");
        }
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
