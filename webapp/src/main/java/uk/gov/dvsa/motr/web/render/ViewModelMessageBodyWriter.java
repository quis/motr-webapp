package uk.gov.dvsa.motr.web.render;

import uk.gov.dvsa.motr.web.viewmodel.ViewModel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("text/html")
public class ViewModelMessageBodyWriter implements MessageBodyWriter<ViewModel> {


    private TemplateEngine templateEngine;

    @Inject
    public ViewModelMessageBodyWriter(TemplateEngine engine) {

        this.templateEngine = engine;
    }


    @Override
    public boolean isWriteable(
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType) {

        return ViewModel.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(
            ViewModel user,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType
    ) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(
            ViewModel viewModel,
            Class<?> type, Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream out
    ) throws IOException, WebApplicationException {

        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write(templateEngine.render(viewModel.getTemplate(), viewModel.getContextMap()));
    }
}