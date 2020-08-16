package me.lortseam.completeconfig.collection;

import me.lortseam.completeconfig.ConfigMap;
import me.lortseam.completeconfig.api.ConfigCategory;
import me.lortseam.completeconfig.exception.IllegalReturnValueException;
import org.apache.commons.lang3.StringUtils;

public class CollectionMap extends ConfigMap<Collection> {

    protected void fill(ConfigCategory category) {
        String categoryID = category.getConfigCategoryID();
        if (StringUtils.isBlank(categoryID)) {
            throw new IllegalReturnValueException("Category ID of " + category.getClass() + " must not be null or blank");
        }
        if (containsKey(categoryID)) {
            throw new IllegalStateException("Duplicate category ID found: " + categoryID);
        }
        Collection collection = new Collection(category);
        if (collection.getEntries().isEmpty() && collection.getCollections().isEmpty()) {
            //TODO
            //LOGGER.warn()
            return;
        }
        put(categoryID, collection);
    }

}
