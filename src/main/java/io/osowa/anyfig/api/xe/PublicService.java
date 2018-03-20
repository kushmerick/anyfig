package io.osowa.anyfig.api.xe;

import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.StatelessService;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.common.Utils;

import io.osowa.anyfig.Anyfig;
import io.osowa.anyfig.Possible;
import io.osowa.anyfig.api.RemoteAPI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// public client-facing service; all other Xe services are intended for Anyfig's internal use only

public class PublicService extends StatelessService {

    public final Anyfig anyfig;

    public static final String SELF_LINK = "/anyfig"; // TODO: Sigh - Can't customize this without major Xe surgery?!

    private static final String KEY = "key";
    private static final String PATH_TEMPLATE = SELF_LINK + "/{" + KEY + '}';

    public PublicService(Anyfig anyfig) {
        this.anyfig = anyfig;
        toggleOption(ServiceOption.URI_NAMESPACE_OWNER, true);
    }

    private boolean enforceAuthorization(Operation op) {
        boolean a = ((Host)getHost()).isAuthorized(op);
        if (!a) {
            op.setStatusCode(Operation.STATUS_CODE_UNAUTHORIZED);
            op.setBody(XeRemoteAPI.InvalidToken.SINGLETON);
            op.complete();
        }
        return a;
    }

    public void handleGet(Operation get) {
        if (!enforceAuthorization(get)) {
            return;
        }
        Map<String,String> parsed = UriUtils.parseUriPathSegments(get.getUri(), PATH_TEMPLATE);
        RemoteAPI.GetResponse response = new RemoteAPI.GetResponse();
        if (parsed.containsKey(KEY)) {
            // case 1: GET /anyfig/X
            String key = parsed.get(KEY);
            boolean got = false;
            try {
                Possible<Object> value = anyfig.remoteGet(key);
                if (value.present()) { // this should definitely be true; but guarad against race conditions
                    response.value = value.get();
                    got = true;
                }
            } catch (Exception ignored) {
                // invalid field
            }
            if (!got) {
                response.error = "Unknown key `" + key + '`';
                get.setStatusCode(Operation.STATUS_CODE_NOT_FOUND);
            }
        } else {
            // case 3: GET /anyfig
            response.values = new HashMap<>();
            for (String key : anyfig.remoteEnumerate()) {
                try {
                    Possible<Object> value = anyfig.remoteGet(key);
                    if (value.present()) { // this should definitely be true; but guarad against race conditions
                        response.values.put(key, value.get());
                    }
                } catch (Exception failure) {
                    // invalid field; we're enumerating our own keys not taking an input from the
                    // user, so we could just just move on; but this definitely shouldn't happen, so
                    // let's fail fast and find out for sure.
                    response.error = "Internal error: Failure while retrieving key `" + key + "`: " + Utils.toString(failure);
                    get.setStatusCode(Operation.STATUS_CODE_INTERNAL_ERROR);
                    break;
                }
            }
        }
        get.setBody(response);
        get.complete();
    }


    public void handlePatch(Operation patch) {
        if (!enforceAuthorization(patch)) {
            return;
        }
        RemoteAPI.PatchRequest request = patch.getBody(RemoteAPI.PatchRequest.class);
        Map<String, String> parsed = UriUtils.parseUriPathSegments(patch.getUri(), PATH_TEMPLATE);
        RemoteAPI.PatchResponse response = new RemoteAPI.PatchResponse();
        Map<String, Object> values = null;
        if (parsed.containsKey(KEY)) {
            values = Collections.singletonMap(parsed.get(KEY), request.value);
        } else if (request.values != null) {
            values = request.values;
        }
        if (values != null) {
            try {
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    anyfig.remoteSet(entry.getKey(), entry.getValue());
                }
            } catch (Exception failure) {
                // TODO: Rollback values that were already set?  Or at least tell the client?  Eeek, yuck, ...
                response.error = "Failure while setting `" + values + "`: " + failure;
                patch.setStatusCode(Operation.STATUS_CODE_BAD_REQUEST); // TODO: return 404 rather than 400 for PATCH /anyfig/X for invalid X
            }
        }
        patch.setBody(response);
        patch.complete();
    }

    public void handleDelete(Operation delete) {
        Operation.failActionNotSupported(delete);
    }

}
