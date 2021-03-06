package de.qabel.core.config;

import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SyncedSettings {
    private Identities identities;
    private Accounts accounts;
    private Set<Contacts> contacts;
    private DropServers dropServers;

    /**
     * Creates an instance of SyncedSettings
     */
    public SyncedSettings() {
        accounts = new Accounts();
        contacts = new HashSet<>();
        dropServers = new DropServers();
        identities = new Identities();
    }

    public void setIdentities(Identities value) {
        identities = value;
    }

    public Identities getIdentities() {
        return identities;
    }

    public Set<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(Contacts value) {
        for (Contacts c : contacts.toArray(new Contacts[contacts.size()])) {
            if (c.getIdentity() == value.getIdentity()) {
                contacts.remove(c);
                break;
            }
        }
        contacts.add(value);
    }

    public void setAccounts(Accounts value) {
        accounts = value;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setDropServers(DropServers value) {
        dropServers = value;
    }

    public DropServers getDropServers() {
        return dropServers;
    }


    /**
     * Serializes this class to a Json String
     *
     * @return Json String
     */
    public String toJson() throws IOException {
        SyncedSettingsTypeAdapter adapter = new SyncedSettingsTypeAdapter();
        return adapter.toJson(this);
    }

    /**
     * Deserializes a Json String
     *
     * @param json Json to deserialize
     * @return SyncedSettings
     */
    public static SyncedSettings fromJson(String json) throws IOException, JsonParseException {
        SyncedSettingsTypeAdapter adapter = new SyncedSettingsTypeAdapter();
        return adapter.fromJson(json);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (accounts == null ? 0 : accounts.hashCode());
        result = prime * result
            + (contacts == null ? 0 : contacts.hashCode());
        result = prime * result
            + (dropServers == null ? 0 : dropServers.hashCode());
        result = prime * result
            + (identities == null ? 0 : identities.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SyncedSettings other = (SyncedSettings) obj;
        if (accounts == null) {
            if (other.accounts != null) {
                return false;
            }
        } else if (!accounts.equals(other.accounts)) {
            return false;
        }
        if (contacts == null) {
            if (other.contacts != null) {
                return false;
            }
        } else if (!contacts.equals(other.contacts)) {
            return false;
        }
        if (dropServers == null) {
            if (other.dropServers != null) {
                return false;
            }
        } else if (!dropServers.equals(other.dropServers)) {
            return false;
        }
        if (identities == null) {
            if (other.identities != null) {
                return false;
            }
        } else if (!identities.equals(other.identities)) {
            return false;
        }
        return true;
    }

}
