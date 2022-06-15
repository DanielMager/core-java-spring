USE `arrowhead`;

REVOKE ALL, GRANT OPTION FROM 'reputationmanager'@'localhost';

GRANT ALL PRIVILEGES ON `arrowhead`.`reputation_rating` TO 'reputationmanager'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`system_` TO 'reputationmanager'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`service_definition` TO 'reputationmanager'@'localhost';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'reputationmanager'@'localhost';

REVOKE ALL, GRANT OPTION FROM 'reputationmanager'@'%';

GRANT ALL PRIVILEGES ON `arrowhead`.`reputation_rating` TO 'reputationmanager'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`system_` TO 'reputationmanager'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`service_definition` TO 'reputationmanager'@'%';
GRANT ALL PRIVILEGES ON `arrowhead`.`logs` TO 'reputationmanager'@'%';

FLUSH PRIVILEGES;
