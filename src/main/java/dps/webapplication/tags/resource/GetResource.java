package dps.webapplication.tags.resource;

import dps.webapplication.resources.Resources;

import javax.inject.Inject;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Reader;

public class GetResource extends SimpleTagSupport {

    @Inject
    Resources resources;

    String name = null;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        Reader reader = resources.getResource(name);
        resources.writeTo(reader,out);
    }
}
