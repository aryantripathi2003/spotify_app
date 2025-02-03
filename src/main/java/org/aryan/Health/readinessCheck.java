package org.aryan.Health;

import com.mongodb.client.MongoClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aryan.Repository.AlbumRepository;
import org.aryan.Service.RedisService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class readinessCheck implements HealthCheck {

    @Inject
    AlbumRepository albumRepository;

    @Inject
    RedisService redisService;

    @Override
    public HealthCheckResponse call() {
        if(albumRepository.isConnected() && redisService.isConnected()) {
            return HealthCheckResponse.up("System is ready");
        } else {
            return HealthCheckResponse.down("System is not ready");
        }
    }
}
