package servlets.admin;

import dbaccess.CaregiverDAO;
import dbaccess.CaregiverServiceDAO;
import dbaccess.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import models.Caregiver;
import models.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/caregivers/add")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,       // 1MB
    maxFileSize = 5 * 1024 * 1024,          // 5MB
    maxRequestSize = 10 * 1024 * 1024       // 10MB
)
public class AdminAddCaregiverServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final CaregiverServiceDAO caregiverServiceDAO = new CaregiverServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer uid = servlets.util.SessionGuard.getUserId(request);
        if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        loadUnassignedServices(request);

        request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer uid = servlets.util.SessionGuard.getUserId(request);
        if (uid == null || !servlets.util.SessionGuard.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/notAuthorized.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // ===== caregiver fields =====
        String name = safe(request.getParameter("name"));
        String bio = safe(request.getParameter("bio"));
        String qualifications = safe(request.getParameter("qualifications"));
        String languages = safe(request.getParameter("languages"));
        String experienceYearsStr = safe(request.getParameter("experience_years"));
        String hourlyRateStr = safe(request.getParameter("hourly_rate"));
        String status = safe(request.getParameter("status"));

        // ===== service dropdown (unassigned only) =====
        String serviceIdStr = safe(request.getParameter("service_id"));

        // ===== validation =====
        if (name.isEmpty()) {
            loadUnassignedServices(request);
            request.setAttribute("error", "Name is required.");
            request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
            return;
        }

        int serviceId;
        try {
            serviceId = Integer.parseInt(serviceIdStr);
        } catch (Exception e) {
            loadUnassignedServices(request);
            request.setAttribute("error", "Please select a service to assign.");
            request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
            return;
        }

        int expYears = 0;
        try { expYears = Integer.parseInt(experienceYearsStr); } catch (Exception ignored) {}

        double hourlyRate = 0.0;
        try { hourlyRate = Double.parseDouble(hourlyRateStr); } catch (Exception ignored) {}

        if (status.isEmpty()) status = "ACTIVE";

        // ✅ photo upload
        String photoPath = saveUploadedPhoto(request);
        if (photoPath.isEmpty()) photoPath = "images/default.png";

        // build object
        Caregiver c = new Caregiver();
        c.setName(name);
        c.setBio(bio);
        c.setQualifications(qualifications);
        c.setLanguages(languages);
        c.setExperienceYears(expYears);
        c.setHourlyRate(hourlyRate);
        c.setPhotoPath(photoPath);
        c.setStatus(status);

        try {
            // 1) insert caregiver, get new id
            int newCaregiverId = caregiverDAO.insertCaregiverReturnId(c);
            if (newCaregiverId <= 0) {
                loadUnassignedServices(request);
                request.setAttribute("error", "Failed to create caregiver.");
                request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
                return;
            }

            // 2) assign caregiver to service via join table
            // (Optional safety: prevent duplicates if service already assigned)
         // 2) assign caregiver to service via join table
            boolean assigned = caregiverServiceDAO.assignCaregiverToService(newCaregiverId, serviceId);

            if (!assigned) {
                loadUnassignedServices(request);
                request.setAttribute("error", "This service already has a caregiver. Please pick another service.");
                request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
                return;
            }

            // 3) update caregiver_name inside the service table
            serviceDAO.updateCaregiverNameForService(serviceId);

            response.sendRedirect(request.getContextPath() + "/admin/caregivers?added=1");


        } catch (Exception e) {
            e.printStackTrace();
            loadUnassignedServices(request);
            request.setAttribute("error", "Failed to add caregiver.");
            request.getRequestDispatcher("/admin/addCaregiver.jsp").forward(request, response);
        }
    }

    // ============================
    // Helpers
    // ============================

    private void loadUnassignedServices(HttpServletRequest request) {
        try {
            List<Service> available = serviceDAO.getUnassignedServices();
            request.setAttribute("availableServices", available);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("availableServices", new ArrayList<Service>());
            request.setAttribute("error", "Unable to load available services.");
        }
    }

    private String saveUploadedPhoto(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("photo");
        if (part == null || part.getSize() <= 0) return "";

        String contentType = part.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            return "";
        }

        String submitted = Paths.get(part.getSubmittedFileName()).getFileName().toString();

        String ext = "";
        int dot = submitted.lastIndexOf('.');
        if (dot >= 0 && dot < submitted.length() - 1) {
            ext = submitted.substring(dot).toLowerCase();
        }

        if (!(ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png") || ext.equals(".webp"))) {
            return "";
        }

        String uploadDir = request.getServletContext().getRealPath("/uploads/caregivers");
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "cg_" + System.currentTimeMillis() + ext;
        File outFile = new File(dir, fileName);

        try (InputStream in = part.getInputStream();
             FileOutputStream out = new FileOutputStream(outFile)) {

            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        }

        return "uploads/caregivers/" + fileName;
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
