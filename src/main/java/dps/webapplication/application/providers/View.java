package dps.webapplication.application.providers;

/*
    TODO: add template to view as a sub-View with additional field for attribute for subview.
    the view provider will look into the view, if it has a template, it will go on looking the next view. It will stop once
    it finds a view that has no template. The template can also be called: extends (it's more general term, and can mean
    that it extends an other view, that also can extend an other view)
*/

public class View {
    String view;

    public View(String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }
}
