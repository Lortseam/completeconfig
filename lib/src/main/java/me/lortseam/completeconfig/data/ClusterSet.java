package me.lortseam.completeconfig.data;

import lombok.extern.log4j.Log4j2;
import me.lortseam.completeconfig.api.ConfigGroup;

@Log4j2(topic = "CompleteConfig")
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
