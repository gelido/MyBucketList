package com.rafaelcarvalho.mybucketlist.Interfaces;

import com.rafaelcarvalho.mybucketlist.util.Modification;

/**
 * Created by Rafael on 07/09/16.
 */
public interface OnListChangeListener {

    void itemChanged(Modification modType);

    void applyChanges();

    void removeChange(Modification mod);
}
