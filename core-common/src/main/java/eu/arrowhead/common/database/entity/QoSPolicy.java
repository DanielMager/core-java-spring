package eu.arrowhead.common.database.entity;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.arrowhead.common.CoreDefaults;

@Entity
@Table(name = "qos_policy")
public class QoSPolicy {

	// =================================================================================================
	// members

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true, length = CoreDefaults.VARCHAR_BASIC)
	private String name;

	@Column(nullable = false, length = CoreDefaults.VARCHAR_EXTENDED)
	private String description;

	@Column(nullable = false, length = CoreDefaults.VARCHAR_BASIC)
	private String evaluator;

	@Convert(converter = JsonConverter.class)
	private Map<String, String> parameters;

	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;

	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSPolicy() {
	}

	// -------------------------------------------------------------------------------------------------
	public QoSPolicy(String name, String description, String evaluator, Map<String, String> parameters) {
		this.name = name;
		this.description = description;
		this.evaluator = evaluator;
		this.parameters = parameters;
	}

	// -------------------------------------------------------------------------------------------------
	@PrePersist
	public void onCreate() {
		this.createdAt = ZonedDateTime.now();
		this.updatedAt = this.createdAt;
	}

	// -------------------------------------------------------------------------------------------------
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = ZonedDateTime.now();
	}

	// -------------------------------------------------------------------------------------------------
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	// -------------------------------------------------------------------------------------------------
	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setEvaluator(final String evaluator) {
		this.evaluator = evaluator;
	}

	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void setCreatedAt(final ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(final ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Converter
	public static class JsonConverter implements AttributeConverter<Map<String, String>, String> {

		private final ObjectMapper om = new ObjectMapper();

		private final Logger logger = LogManager.getLogger(JsonConverter.class);

		@Override
		public String convertToDatabaseColumn(Map<String, String> attribute) {
			try {
				return om.writeValueAsString(attribute);
			} catch (JsonProcessingException e) {
				logger.error("Error converting parameters to json database column!", e);
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, String> convertToEntityAttribute(String dbData) {
			try {
				return om.readValue(dbData, HashMap.class);
			} catch (IOException e) {
				logger.error("Error reading json database column '{}'", dbData, e);
				return null;
			}
		}
	}
}