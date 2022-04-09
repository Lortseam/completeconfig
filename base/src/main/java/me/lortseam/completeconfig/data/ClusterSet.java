package me.lortseam.completeconfig.data;

import me.lortseam.completeconfig.api.ConfigGroup;

public class ClusterSet extends SortedSet<Cluster> {

    protected ClusterSet(Parent parent) {
        super(parent);
    }

    void resolve(ConfigGroup group) {
        Cluster cluster = new Cluster(parent, group);
        cluster.resolveContainer(group);
        if (cluster.isEmpty()) return;
        add(cluster);
    }

}
