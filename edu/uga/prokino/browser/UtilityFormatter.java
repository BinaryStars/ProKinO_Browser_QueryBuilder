package edu.uga.prokino.browser;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import freemarker.template.Template;

public class UtilityFormatter
{
    // ProKinO browser object; maintains point of focus (current resource)
    //
    private Browser          browser            = null;
    
    // constructor
    //
    public UtilityFormatter()
    {
        // create a new browser in verbose mode
        //
        browser = new Browser( true );
    }
    
    // output an error page (HTML)
    //
    public void outputError_html(
                                 PrintWriter writer,
                                 ServletContext servletContext,
                                 String errorMsg
                                )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "prokino_error.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web app.
            //
            template = cfg.getTemplate( templateName );

            Map<String, Object> dataModel = null;
            dataModel = new HashMap<String, Object>();
            
            dataModel.put( "title", "ProKinO Error" );
            dataModel.put( "version", browser.getVersionInfo() );
            dataModel.put( "date", browser.getDate() );

            dataModel.put( "error_message", errorMsg );

            // Process the template, using the values from the data-model
            // the instance of the template (with substituted values) will be
            // written to the parameter writer (servlet's output)
            template.process( dataModel, writer );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }

    }

    // output an error page (HTML)
    //
    public void outputStringResult_html(
                                         PrintWriter writer,
                                         ServletContext servletContext,
                                         String result
                                       )
    {
        freemarker.template.Configuration cfg = null;
        Template template = null;
        String templateDir = "WEB-INF/templates";
        String templateName = "string_result.html";

        try {

            // Initialize the FreeMarker configuration;
            // - Create a configuration instance
            cfg = new freemarker.template.Configuration();

            cfg.setServletContextForTemplateLoading( servletContext,
                    templateDir );

            // Load templates from the WEB-INF/templates directory of the Web
            // app.
            //
            template = cfg.getTemplate( templateName );

            Map<String, Object> dataModel = null;
            dataModel = new HashMap<String, Object>();
            
            dataModel.put( "title", "Result" );
            dataModel.put( "version", browser.getVersionInfo() );
            dataModel.put( "date", browser.getDate() );

            dataModel.put( "result", result );

            // Process the template, using the values from the data-model
            // the instance of the template (with substituted values) will be
            // written to the parameter writer (servlet's output)
            template.process( dataModel, writer );

            writer.flush();

        }
        catch( Exception e ) {
            e.printStackTrace( System.err );
        }

    }

}
