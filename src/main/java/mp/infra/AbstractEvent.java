package mp.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import mp.MybookApplication;

@Getter
@Setter
public abstract class AbstractEvent {

    private String eventType;
    private Long timestamp;

    public AbstractEvent(Object aggregate) {
        this();
        try {
            // 기본 속성 복사 (Optional)
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(aggregate);
            mapper.readerForUpdating(this).readValue(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AbstractEvent() {
        this.eventType = this.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
    }

    public void publish() {
        KafkaTemplate<String, Object> kafkaTemplate =
                MybookApplication.applicationContext.getBean(KafkaTemplate.class);
        kafkaTemplate.send("mp", this);
    }

    public void publishAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                AbstractEvent.this.publish();
            }
        });
    }

    public boolean validate() {
        return getEventType().equals(this.getClass().getSimpleName());
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }
    }
}

