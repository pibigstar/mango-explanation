package mango.config;

import java.io.Serializable;

/**
 *
 *  这个应该是个基类，所有的配置类统一继承此类
 * @author Ricky Fung
 */
public class AbstractConfig implements Serializable {

    private static final long serialVersionUID = -6047235443917923115L;

    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
