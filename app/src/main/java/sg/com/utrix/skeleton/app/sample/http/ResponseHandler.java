package sg.com.utrix.skeleton.app.sample.http;

import android.net.Uri;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Enables custom handling of HttpResponse and the entities they contain.
 */
public interface ResponseHandler {
    void handleResponse(HttpResponse response, Uri uri)
            throws IOException;
}
