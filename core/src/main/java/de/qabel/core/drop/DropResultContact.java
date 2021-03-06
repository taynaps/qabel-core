package de.qabel.core.drop;

import de.qabel.core.config.Contact;

import java.util.Arrays;

/**
 * Class DropResultContact: Save the return value of a contact
 */
public class DropResultContact {
    private Contact contact;
    private int[] errorCode = new int[0];
    private boolean success;

    /**
     * Constructor
     *
     * @param contact Contact object
     */
    DropResultContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * Add an received error code to the error code list
     *
     * @param errorCode Received error code
     * @return true when you have received error code 200 one time
     */
    boolean addErrorCode(int errorCode) {
        if (errorCode == 200) {
            success = true;
        }

        this.errorCode = addElement(this.errorCode, errorCode);

        return success;
    }

    /**
     * Get contact
     *
     * @return Contact object
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Return error code
     *
     * @return Error code
     */
    public int[] getErrorCode() {
        return errorCode;
    }

    /**
     * Get the return state of the shipping
     *
     * @return Return state
     */
    public boolean isSuccess() {
        return success;
    }

    private int[] addElement(int[] array, int element) {
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = element;
        return array;
    }
}
