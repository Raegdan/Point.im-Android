package org.itishka.pointim;

import org.itishka.pointim.api.ConnectionManager;

/**
 * Created by Татьяна on 21.10.2014.
 */
public class RecentFragment extends PostListFragment {
    @Override
    protected void update() {
        ConnectionManager.getInstance().pointService.getRecent(getCallback());
    }
}
