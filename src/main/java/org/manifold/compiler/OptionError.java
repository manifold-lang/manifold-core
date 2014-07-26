package org.manifold.compiler;

public class OptionError extends Error {
    private static final long serialVersionUID = -6379060530672263064L;

    private String message;
    
    public OptionError(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return "error while handling command-line options:"
                + this.message;
    }
    
}
