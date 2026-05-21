package com.mycompany.northwindmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/suppliers/*")
public class SupplierServlet extends HttpServlet {

    private SupplierDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new SupplierDAO(HibernateUtil.getEntityManagerFactory());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setJsonResponse(resp);

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<Supplier> suppliers = dao.findAll();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < suppliers.size(); i++) {
                    if (i > 0) json.append(",");
                    json.append(toJson(suppliers.get(i)));
                }
                json.append("]");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(json.toString());
            } catch (Exception e) {
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Errore recupero fornitori: " + e.getMessage());
            }
            return;
        }

        int id = extractId(req, resp);
        if (id == -1) return;

        Supplier supplier = dao.findById(id);
        if (supplier == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Fornitore con ID " + id + " non trovato");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(toJson(supplier));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setJsonResponse(resp);

        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "POST non accetta ID nell'URL. Usa /api/suppliers/");
            return;
        }

        String body        = readBody(req);
        String companyName = extractJsonField(body, "companyName");
        String contactName = extractJsonField(body, "contactName");
        String country     = extractJsonField(body, "country");

        if (companyName == null || companyName.isBlank()) {
            sendError(resp, 422,
                    "Il campo 'companyName' e' obbligatorio");
            return;
        }

        Supplier supplier = new Supplier();
        supplier.setCompanyName(companyName);
        if (contactName != null) supplier.setContactName(contactName);
        if (country     != null) supplier.setCountry(country);

        try {
            Supplier created = dao.save(supplier);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(toJson(created));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore creazione fornitore: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setJsonResponse(resp);

        int id = extractId(req, resp);
        if (id == -1) return;

        Supplier existing = dao.findById(id);
        if (existing == null) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                    "Fornitore con ID " + id + " non trovato");
            return;
        }

        String body        = readBody(req);
        String companyName = extractJsonField(body, "companyName");
        String contactName = extractJsonField(body, "contactName");
        String country     = extractJsonField(body, "country");

        if (companyName == null || companyName.isBlank()) {
            sendError(resp, 422,
                    "Il campo 'companyName' e' obbligatorio");
            return;
        }

        existing.setCompanyName(companyName);
        if (contactName != null) existing.setContactName(contactName);
        if (country     != null) existing.setCountry(country);

        try {
            Supplier updated = dao.update(existing);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(toJson(updated));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore aggiornamento: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        setJsonResponse(resp);

        int id = extractId(req, resp);
        if (id == -1) return;

        try {
            boolean ok = dao.delete(id);
            if (!ok) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND,
                        "Fornitore con ID " + id + " non trovato");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Errore cancellazione: " + e.getMessage());
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void setJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private int extractId(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "ID mancante. Usa /api/suppliers/{id}");
            return -1;
        }
        try {
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST,
                    "ID non valido: deve essere un numero intero");
            return -1;
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString().trim();
    }

    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx == -1) return null;
        int colon = json.indexOf(":", idx);
        int start = json.indexOf("\"", colon) + 1;
        int end   = json.indexOf("\"", start);
        if (start <= 0 || end <= 0) return null;
        return json.substring(start, end);
    }

    private String toJson(Supplier s) {
        return String.format(
            "{\"supplierID\":%d,\"companyName\":\"%s\",\"contactName\":\"%s\"," +
            "\"contactTitle\":\"%s\",\"address\":\"%s\",\"city\":\"%s\"," +
            "\"region\":\"%s\",\"postalCode\":\"%s\",\"country\":\"%s\"," +
            "\"phone\":\"%s\",\"fax\":\"%s\",\"homePage\":\"%s\"}",
            s.getSupplierID(),
            esc(s.getCompanyName()),
            esc(s.getContactName()),
            esc(s.getContactTitle()),
            esc(s.getAddress()),
            esc(s.getCity()),
            esc(s.getRegion()),
            esc(s.getPostalCode()),
            esc(s.getCountry()),
            esc(s.getPhone()),
            esc(s.getFax()),
            esc(s.getHomePage())
        );
    }

    // evita null e virgolette rotte nel JSON
    private String esc(String v) {
        if (v == null) return "";
        return v.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void sendError(HttpServletResponse resp, int status, String message)
            throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(
            "{\"error\":" + status + ",\"message\":\"" + message + "\"}"
        );
    }
}