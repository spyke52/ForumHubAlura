CREATE TABLE system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    logger VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    ip_address VARCHAR(45),
    endpoint VARCHAR(255),
    INDEX idx_logs_timestamp (timestamp),
    INDEX idx_logs_level (level)
);