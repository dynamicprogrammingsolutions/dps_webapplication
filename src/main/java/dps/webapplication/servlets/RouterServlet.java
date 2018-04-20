package dps.webapplication.servlets;

import dps.logging.HasLogger;
import dps.webapplication.configuration.Settings;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@WebServlet(name = "RouterServlet")
public class RouterServlet extends HttpServlet implements HasLogger {

    Pattern indexPattern = Pattern.compile("^(?:/|/index|/index\\.html)?$");
    Pattern jspPattern = Pattern.compile("^/(index|(?:pages/[a-zA-Z0-9-_/]+))$");
    Pattern resourcePattern = Pattern.compile("^/(?:scripts|images|styles|views|[a-zA-Z0-9-_]+\\.html)(?:/.*)?$");
    Pattern sitePattern = Pattern.compile("^/(?:[a-zA-Z]*)(?:/.*)?$");

    final Pattern replaceMatch = Pattern.compile("\\$");

    int MAXAGE = 0;

    String indexPath = "/WEB-INF/index.jsp";
    String jspPath = "/WEB-INF/$.jsp";
    String resourcePath = "/resources";
    String applicationPath = "/application";

    @Inject SessionOfRequest sessionOfRequest;
    @Inject Settings settings;

    @Override
    public void init() throws ServletException {

        String indexPatternStr = getInitParameter("indexPattern");
        String jspPatternStr = getInitParameter("jspPattern");
        String resourcePatternStr = getInitParameter("resourcePattern");
        String sitePatternStr = getInitParameter("applicationPattern");

        if (indexPatternStr == null || jspPatternStr == null || resourcePatternStr == null || sitePatternStr == null) {
            String nulls = "";
            if (indexPatternStr == null) nulls = nulls + "indexPattern is not set. ";
            if (jspPatternStr == null) nulls = nulls + "jspPattern is not set. ";
            if (resourcePatternStr == null) nulls = nulls + "resourcePattern is not set. ";
            if (sitePatternStr == null) nulls = nulls + "applicationPattern is not set. ";

            throw new ServletException("Required initialization parameters are not set. "+nulls);
        }

        indexPattern = Pattern.compile(indexPatternStr);
        jspPattern = Pattern.compile(jspPatternStr);
        resourcePattern = Pattern.compile(resourcePatternStr);
        sitePattern = Pattern.compile(sitePatternStr);

        String indexPathStr = getInitParameter("indexPath");
        if (indexPath != null) indexPath = indexPathStr;

        String jspPathStr = getInitParameter("jspPath");
        if (jspPath != null) jspPath = jspPathStr;

        String resourcePathStr = getInitParameter("resourcePath");
        if (resourcePath != null) resourcePath = resourcePathStr;

        String applicationPathStr = getInitParameter("applicationPath");
        if (applicationPath != null) applicationPath = applicationPathStr;

        String maxAge = getInitParameter("maxAge");
        if (maxAge != null) MAXAGE = Integer.valueOf(maxAge);

    }

    private void setRoot(String method, HttpServletRequest request, HttpServletResponse response)
    {
        sessionOfRequest.setSession(request.getSession());
        if ("GET".equals(method) || "POST".equals(method)) {
            String absoluteRoot = settings.getHost()+settings.getRoot();
            request.setAttribute("root",settings.getRoot());
            request.setAttribute("absoluteRoot",absoluteRoot);
            request.setAttribute("self",request.getRequestURL());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (subService(indexPattern,-1,indexPath,req,resp,this::setRoot)) return;
        if (subService(jspPattern,1,jspPath,req,resp,this::setRoot)) return;
        if (subService(resourcePattern,0,resourcePath,req,resp,(method,request,response) -> {
            if ("GET".equals(method) || "HEAD".equals(method)) {
                response.addHeader("Cache-Control","max-age="+MAXAGE);
                response.addHeader("Cache-Control","public");
            }
        })) return;
        if (subService(sitePattern,0,applicationPath,req,resp,this::setRoot)) return;
        resp.sendError(404);
    }

    private boolean subService(Pattern pattern, int groupIdx, String subResource, HttpServletRequest req, HttpServletResponse resp, AddHeaders addHeaders) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) pathInfo = "";
        Matcher matcher = pattern.matcher(pathInfo);
        if (matcher.find()) {
            logInfo("found match: "+pattern+" "+pathInfo);
            if (addHeaders != null) {
                addHeaders.addHeaders(req.getMethod(),req,resp);
            }
            String forwardTo = subResource;
            Matcher matcherReplace = replaceMatch.matcher(subResource);
            if (matcherReplace.find()) {
                forwardTo = matcherReplace.replaceAll((groupIdx >= 0 ? matcher.group(groupIdx) : ""));
            } else {
                forwardTo = forwardTo + (groupIdx >= 0 ? matcher.group(groupIdx) : "");
            }
            logInfo("forwarding to: "+forwardTo);
            RequestDispatcher dispatcher = req.getRequestDispatcher(forwardTo);
            dispatcher.forward(req,resp);
            return true;
        }
        return false;
    }

    interface AddHeaders {
        void addHeaders(String method, HttpServletRequest request, HttpServletResponse response);
    }

}
