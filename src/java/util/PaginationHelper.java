package util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Server-side pagination helper for list screens.
 */
public final class PaginationHelper {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private PaginationHelper() {}

    public static <T> List<T> paginate(HttpServletRequest req, List<T> fullList) {
        return paginate(req, fullList, DEFAULT_PAGE_SIZE);
    }

    public static <T> List<T> paginate(HttpServletRequest req, List<T> fullList, int defaultPageSize) {
        if (fullList == null) {
            fullList = Collections.emptyList();
        }

        int pageSize = parsePositive(req.getParameter("pageSize"), defaultPageSize);
        if (pageSize > 50) pageSize = 50;

        int totalItems = fullList.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));

        int page = parsePositive(req.getParameter("page"), 1);
        if (page > totalPages) page = totalPages;

        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, totalItems);
        List<T> pageItems = (from >= totalItems)
                ? Collections.emptyList()
                : fullList.subList(from, to);

        req.setAttribute("currentPage", page);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("pageFrom", totalItems == 0 ? 0 : from + 1);
        req.setAttribute("pageTo", to);
        return pageItems;
    }

    /** Append URL-encoded query params as &amp;key=value pairs (no leading '?'). */
    public static String buildQuery(String... keyValues) {
        StringBuilder sb = new StringBuilder();
        if (keyValues == null) return "";
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            String key = keyValues[i];
            String val = keyValues[i + 1];
            if (key == null || val == null || val.trim().isEmpty()) continue;
            sb.append('&').append(encode(key)).append('=').append(encode(val.trim()));
        }
        return sb.toString();
    }

    private static int parsePositive(String raw, int fallback) {
        try {
            int n = Integer.parseInt(raw);
            return n > 0 ? n : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
