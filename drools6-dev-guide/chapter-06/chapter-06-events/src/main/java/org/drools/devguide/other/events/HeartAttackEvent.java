package org.drools.devguide.other.events;

import java.io.Serializable;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;

@Role(Role.Type.EVENT)
@Expires("30m")
public class HeartAttackEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    public HeartAttackEvent() {
        super();
    }
}
