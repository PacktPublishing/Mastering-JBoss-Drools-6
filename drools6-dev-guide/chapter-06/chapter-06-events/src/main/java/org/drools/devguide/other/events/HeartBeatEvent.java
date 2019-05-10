package org.drools.devguide.other.events;

import java.io.Serializable;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("10m")
public class HeartBeatEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public HeartBeatEvent() {
        super();
    }
}

