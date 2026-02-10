/*package servlets.admin;

import dbaccess.AdminDAO;
import dbaccess.ServiceCategoryDAO;
import models.Service;
import models.ServiceCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;


@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024,      // 1MB
	    maxFileSize = 5 * 1024 * 1024,        // 5MB
	    maxRequestSize = 10 * 1024 * 1024     // 10MB
	)
	@WebServlet("/admin/services/add")
	public class AdminAddServiceServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();
    private final ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();

    // ✅ GET = show form, load categories (NO SQL in JSP)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            List<ServiceCategory> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
        } catch (Exception e) {
            request.setAttribute("catError", "Unable to load categories.");
        }

        request.getRequestDispatcher("/admin/addService.jsp").forward(request, response);
    }

    // ✅ POST = submit form, create service
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!AdminBase.requireAdmin(request, response)) return;

        try {
            request.setCharacterEncoding("UTF-8");

            Service s = new Service();
            s.setCategoryId(Integer.parseInt(request.getParameter("category_id")));
            s.setServiceName(request.getParameter("service_name"));
            s.setServiceDescription(request.getParameter("service_description"));
            s.setPrice(new BigDecimal(request.getParameter("price")));
            s.setDuration(request.getParameter("duration"));

            String savedImagePath = saveUploadedServiceImage(request, "service_image_file");
            if (savedImagePath == null) {
                response.sendRedirect(request.getContextPath() + "/admin/services/add?err=noImage");
                return;
            }

            s.setServiceImage(savedImagePath);

            adminDAO.createService(s);

            response.sendRedirect(request.getContextPath() + "/admin/services?created=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/services?err=add");
        }
    }
    private String saveUploadedServiceImage(HttpServletRequest request, String fieldName) throws Exception {

        Part filePart = request.getPart(fieldName);

        // no file uploaded
        if (filePart == null || filePart.getSize() == 0) return null;

        // must be image
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return null;

        // get original filename safely
        String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        // extension
        String ext = "";
        int dot = submitted.lastIndexOf('.');
        if (dot >= 0) ext = submitted.substring(dot).toLowerCase();

        // allow only these extensions
        if (!(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".webp"))) {
            return null;
        }

        // generate safe filename
        String newName = "svc_" + UUID.randomUUID().toString().replace("-", "") + ext;

        // save folder inside webapp
        String uploadDir = request.getServletContext().getRealPath("/uploads/services");
        if (uploadDir == null) return null;

        Path dirPath = Path.of(uploadDir);
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        // write file
        Path savePath = dirPath.resolve(newName);
        filePart.write(savePath.toString());

        // return DB path
        return "/uploads/services/" + newName;
    }


}
*/

package servlets.admin;

import dbaccess.AdminDAO;
import dbaccess.ServiceCategoryDAO;
import dbaccess.ServiceDAO;
import models.Service;
import models.ServiceCategory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
		maxFileSize = 5 * 1024 * 1024, // 5MB
		maxRequestSize = 10 * 1024 * 1024 // 10MB
)
@WebServlet("/admin/services/add")
public class AdminAddServiceServlet extends HttpServlet {

	private final AdminDAO adminDAO = new AdminDAO();
	private final ServiceCategoryDAO categoryDAO = new ServiceCategoryDAO();
	private final ServiceDAO serviceDAO = new ServiceDAO(); // ✅ for inserting time slots

	// ✅ GET = show form, load categories
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!AdminBase.requireAdmin(request, response))
			return;

		try {
			List<ServiceCategory> categories = categoryDAO.getAllCategories();
			request.setAttribute("categories", categories);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("catError", "Unable to load categories.");
		}

		request.getRequestDispatcher("/admin/addService.jsp").forward(request, response);
	}

	// ✅ POST = create service + insert time slots
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!AdminBase.requireAdmin(request, response))
			return;

		try {
			request.setCharacterEncoding("UTF-8");

			Service s = new Service();
			s.setCategoryId(Integer.parseInt(request.getParameter("category_id")));
			s.setServiceName(request.getParameter("service_name"));
			s.setServiceDescription(request.getParameter("service_description"));
			s.setPrice(new BigDecimal(request.getParameter("price")));
			s.setDuration(request.getParameter("duration"));

			// ✅ image upload (required in your current logic)
			String savedImagePath = saveUploadedServiceImage(request, "service_image_file");
			if (savedImagePath == null) {
				response.sendRedirect(request.getContextPath() + "/admin/services/add?err=noImage");
				return;
			}
			s.setServiceImage(savedImagePath);

			// ✅ read time slots from form (arrays)
			String[] slotLabels = request.getParameterValues("slot_label");
			String[] slotTimes = request.getParameterValues("slot_time");

			boolean hasAtLeastOne = false;

			if (slotLabels != null && slotTimes != null) {
				for (int i = 0; i < slotLabels.length; i++) {
					String lbl = slotLabels[i] != null ? slotLabels[i].trim() : "";
					String t = slotTimes[i] != null ? slotTimes[i].trim() : "";

					if (!lbl.isEmpty() || !t.isEmpty()) {
						hasAtLeastOne = true;
						break;
					}
				}
			}

			if (!hasAtLeastOne) {
				response.sendRedirect(request.getContextPath() + "/admin/services/add?err=noSlotFilled");
				return;
			}

			// ✅ create service and get new service_id
			int newServiceId = adminDAO.createService(s);

			// ✅ insert time slots for that service
			serviceDAO.insertTimeSlotsForService(newServiceId, slotLabels, slotTimes);

			response.sendRedirect(request.getContextPath() + "/admin/services?created=1");

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/admin/services?err=add");
		}
	}

	private String saveUploadedServiceImage(HttpServletRequest request, String fieldName) throws Exception {

		Part filePart = request.getPart(fieldName);

		// no file uploaded
		if (filePart == null || filePart.getSize() == 0)
			return null;

		// must be image
		String contentType = filePart.getContentType();
		if (contentType == null || !contentType.startsWith("image/"))
			return null;

		// get original filename safely
		String submitted = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

		// extension
		String ext = "";
		int dot = submitted.lastIndexOf('.');
		if (dot >= 0)
			ext = submitted.substring(dot).toLowerCase();

		// allow only these extensions
		if (!(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".webp"))) {
			return null;
		}

		// generate safe filename
		String newName = "svc_" + UUID.randomUUID().toString().replace("-", "") + ext;

		// save folder inside webapp
		String uploadDir = request.getServletContext().getRealPath("/uploads/services");
		if (uploadDir == null)
			return null;

		Path dirPath = Path.of(uploadDir);
		if (!Files.exists(dirPath))
			Files.createDirectories(dirPath);

		// write file
		Path savePath = dirPath.resolve(newName);
		filePart.write(savePath.toString());

		// return DB path
		return "/uploads/services/" + newName;
	}
}
