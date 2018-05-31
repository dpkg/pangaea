package com.cvent.pangaea;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;


/**
 * Construct BadRequest response in case of environment specific exception
 */
public class MultiEnvSupportExceptionMapper implements ExceptionMapper<MultiEnvSupportException> {

    private static final Response.Status RESPONSE_STATUS = Response.Status.BAD_REQUEST;

    @Override
    public Response toResponse(MultiEnvSupportException e) {
        return Response.status(RESPONSE_STATUS).entity(new ErrorMessage(RESPONSE_STATUS.getStatusCode(), 
                e.getMessage())).build();
    }

    /**
     * Encapsulation of what our standard HTTP error messages look like (copied from dropwizard)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ErrorMessage {

        private final int code;
        private final String message;
        private final String details;

        ErrorMessage(String message) {
            this(RESPONSE_STATUS.getStatusCode(), message);
        }

        ErrorMessage(int code, String message) {
            this(code, message, null);
        }

        @JsonCreator
        ErrorMessage(@JsonProperty("code") int code, @JsonProperty("message") String message,
                @JsonProperty("details") String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        @JsonProperty("code")
        public Integer getCode() {
            return code;
        }

        @JsonProperty("message")
        public String getMessage() {
            return message;
        }

        @JsonProperty("details")
        public String getDetails() {
            return details;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }

            final ErrorMessage other = (ErrorMessage) obj;
            return Objects.equals(code, other.code)
                    && Objects.equals(message, other.message)
                    && Objects.equals(details, other.details);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, message, details);
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.reflectionToString(this);
        }
    }
}
