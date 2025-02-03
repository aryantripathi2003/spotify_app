package org.aryan.Mapper;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.aryan.Exception.BusinessException;
import org.aryan.Exception.ErrorMessage;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException e) {
        ErrorMessage errorMessage=new ErrorMessage();
        errorMessage.setStatus(e.getStatus());
        errorMessage.setMessage(e.getMessage());

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
