INSERT IGNORE INTO roles (id, name, admin, customer, user) VALUES
                                                               (1, 'ROLE_ADMIN', 1, 0, 0),
                                                               (2, 'ROLE_CUSTOMER', 0, 1, 0),
                                                               (3, 'ROLE_USER', 0, 0, 1);

INSERT INTO user_roles (user_id,role_id) VALUES
                                             (1,1)