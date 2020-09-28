package cn.error0.Resolver;

import cn.error0.Model.IModel;
import com.sun.istack.internal.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class BaseResolver  {
    @Nullable
    private String prefix;
    private String suffix;
    private String view;

    private IModel model;
    public BaseResolver() { }
    public void setModel(IModel model)
    {
        this.model=model;
    }

    public IModel getModel() {
        return model;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void forward(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Set<String> keys = model.keySet();
            for( Iterator<String> iterator = keys.iterator();iterator.hasNext();)
            {
                String key=iterator.next();
                req.setAttribute(key,model.get(key));
            }

            req.getRequestDispatcher(prefix+view+suffix).forward(req, resp);
        } catch (ServletException |IOException e) {
            e.printStackTrace();
        }
    }
}
