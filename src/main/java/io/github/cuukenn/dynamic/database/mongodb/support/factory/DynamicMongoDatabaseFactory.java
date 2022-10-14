package io.github.cuukenn.dynamic.database.mongodb.support.factory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.github.cuukenn.dynamic.database.mongodb.support.DynamicMongoContext;
import io.github.cuukenn.dynamic.database.mongodb.support.toolkit.DynamicMongoDatabaseContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author changgg
 */
public class DynamicMongoDatabaseFactory extends SimpleMongoClientDatabaseFactory {
    private final Logger logger = LoggerFactory.getLogger(DynamicMongoDatabaseFactory.class);
    private final DynamicMongoClientFactory dynamicMongoClientFactory;

    public DynamicMongoDatabaseFactory(MongoClient mongoClient, String databaseName, DynamicMongoClientFactory dynamicMongoClientFactory) {
        super(mongoClient, databaseName);
        this.dynamicMongoClientFactory = dynamicMongoClientFactory;
    }

    @Override
    protected MongoClient getMongoClient() {
        final DynamicMongoContext context = getContext();
        final MongoClient client = StringUtils.hasText(context.getInstanceId()) ? this.dynamicMongoClientFactory.getDynamicMongoClient(context.getInstanceId()) : super.getMongoClient();
        Assert.notNull(client, "no dynamic mongo database instance found for " + context);
        return client;
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        final DynamicMongoContext context = getContext();
        return StringUtils.hasText(context.getDatabaseName()) ? getMongoDatabase(context.getDatabaseName()) : super.getMongoDatabase();
    }

    protected DynamicMongoContext getContext() {
        final DynamicMongoContext context = DynamicMongoDatabaseContextHolder.peek();
        if (logger.isDebugEnabled()) {
            logger.debug("excepted dynamic mongodb instance is [{}],use default mongo client:[{}]", context, !StringUtils.hasText(context.getInstanceId()));
        }
        return context;
    }
}