package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.var;

@Service
public class DemoWriteBackService {
    private static final Logger logger = LoggerFactory.getLogger(DemoWriteBackService.class);

    private static final String WRITE_BACK_CACHE_KEY = "sample:write_back";
    private final SampleEntityRepository sampleEntityRepository;

    @Autowired
    private RedisTemplate<String, SampleEntity> redisTemplate;

    public DemoWriteBackService(SampleEntityRepository sampleEntityRepository, RedisTemplate<String, SampleEntity> redisTemplate){
        this.sampleEntityRepository = sampleEntityRepository;
        this.redisTemplate = redisTemplate;
    }

    // 1. Entity creation method. Let the entity save at redis cache store.
    @Transactional
    public SampleEntity create (int paramId, String paramValue){
        final var sampleEntity = new SampleEntity();

        logger.info("### Create Entity Called : {}, {} ###", paramId, paramValue);
        sampleEntity.setId(paramId);
        sampleEntity.setValue(paramValue);

        // Write common data in cache-store.
        redisTemplate.boundValueOps(Integer.toString(sampleEntity.getId())).set(sampleEntity);

        // Write data what to sync with databse in cache-store.
        redisTemplate.boundSetOps(WRITE_BACK_CACHE_KEY).add(sampleEntity);

        return sampleEntity;
    }

    // 2. Back-ground scheduler method. The schduler will retreive entities from redis cache store. And will write down on database;
    @Scheduled(fixedDelay = 60000)
    public void writeBack() {

        // Scan datas in set names "write_back".
        final var amountOfsampleEntityToPersist = redisTemplate.boundSetOps(WRITE_BACK_CACHE_KEY).size();
        if (amountOfsampleEntityToPersist == null || amountOfsampleEntityToPersist == 0) {
            logger.info("None entity to write back from cache to database");
            return;
        }

        logger.info("Found {} Entity to write back from cache to database", amountOfsampleEntityToPersist);
        final var setOperations = redisTemplate.boundSetOps(WRITE_BACK_CACHE_KEY);
        final var scanOptions = ScanOptions.scanOptions().build();

        try (final var cursor = setOperations.scan(scanOptions)) {
            assert cursor != null;
            while (cursor.hasNext()) {
                final var sampleEntity = cursor.next();

                // Write the datas to sync on database.
                sampleEntityRepository.save(sampleEntity);
                logger.info("Entity saved (Entity={})", sampleEntity);

                // Remove datas on set names "write_back".
                redisTemplate.boundSetOps(WRITE_BACK_CACHE_KEY).remove(sampleEntity);
                logger.info("Entity removed from {} set (Entity={})", WRITE_BACK_CACHE_KEY, sampleEntity);
            }
            logger.info("Persisted {} entities in the database", amountOfsampleEntityToPersist);
        } catch (RuntimeException exception) {
            logger.error("Error reading {} set from Redis", WRITE_BACK_CACHE_KEY, exception);
        }
    }

    // 3. Entity Select Called. This pattern means read-through.
    public SampleEntity findOne(int sampleEntityId) {
        final var sampleEntityOnCache = redisTemplate.boundValueOps(Integer.toString(sampleEntityId)).get();
        if (sampleEntityOnCache != null) {
            logger.info("Entity retrieved from cache (EntityId={})", sampleEntityId);
            return sampleEntityOnCache;
        }

        final var sampleEntityNotCached = sampleEntityRepository.findById(sampleEntityId);
        if (sampleEntityNotCached.isPresent()) {
            logger.info("Entity retrieved from database (EntityId={})", sampleEntityId);

            final var sampleEntity = sampleEntityNotCached.get();
            redisTemplate.boundValueOps(Integer.toString(sampleEntity.getId())).set(sampleEntity);
            redisTemplate.boundSetOps(WRITE_BACK_CACHE_KEY).add(sampleEntity);
            logger.info("Entity cached (key={}, value={})", sampleEntityId, sampleEntity);

            return sampleEntity;
        }

        logger.info("Entity not found (Entity={})", sampleEntityId);
        throw new EntityNotFoundException(sampleEntityId);
    }

}
    