package servlets.admin;

import dbaccess.CaregiverDAO;
import models.Caregiver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 10 * 1024 * 1024
)
@WebServlet("/admin/caregivers/edit")
public class AdminEditCaregiverServlet extends HttpServlet {

    private final CaregiverDAO caregiverDAO = new CaregiverDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Caregiver c = caregiverDAO.getByIdAdmin(id);

            if (c == null) {
                response.sendRedirect(request.getContextPath() + "/admin/caregivers?err=notfound");
                return;
            }

            request.setAttribute("caregiver", c);
            request.getRequestDispatcher("/admin/editCaregiver.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/caregivers?err=badid");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            request.setCharacterEncoding("UTF-8");

            int caregiverId = Integer.parseInt(request.getParameter("caregiver_id"));

            Caregiver c = new Caregiver();
            c.setCaregiverId(caregiverId);
            c.setName(request.getParameter("name"));
            c.setBio(request.getParameter("bio"));
            c.setQualifications(request.getParameter("qualifications"));
            c.setLanguages(request.getParameter("languages"));
            c.setExperienceYears(Integer.parseInt(request.getParameter("experience_years")));
            c.setHourlyRate(Double.parseDouble(request.getParameter("hourly_rate")));
            c.setStatus(request.getParameter("status"));

            String existingPhoto = request.getParameter("existing_photo");
            String newPhotoPath = saveUploadedCaregiverPhoto(request, "photo_file");

            if (newPhotoPath != null) c.setPhotoPath(newPhotoPath);
            else c.setPhotoPath(existingPhoto); // keep old photo

            boolean ok = caregiverDAO.updateCaregiver(c);

            if (ok) {
                response.sendRedirect(request.getContextPath() + "/admin/caregivers?updated=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/caregivers?err=update");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/caregivers?err=update");
        }
    }

    private String saveUploadedCaregiverPhoto(HttpServletRequest request, String fieldName) throws Exception {

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

        String newName = "cg_" + UUID.randomUUID().toString().replace("-", "") + ext;

        String uploadDir = request.getServletContext().getRealPath("/uploads/caregivers");
        if (uploadDir == null) return null;

        Path dirPath = Path.of(uploadDir);
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        Path savePath = dirPath.resolve(newName);
        filePart.write(savePath.toString());

        return "/uploads/caregivers/" + newName;
    }
}
