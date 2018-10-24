package modules;

import com.google.inject.AbstractModule;
import play.api.cache.redis.RecoveryPolicy;

/**
 * ExampleMapper . Created at 8/31/2018 2:30 PM by @author Timur Isachenko
 * @isatimur | † be patient and test it! †
 */
public class BatisModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RecoveryPolicy.class).to(CustomRecoveryPolicy.class);
    }
}
