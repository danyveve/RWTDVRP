CREATE TABLE IF NOT EXISTS user_role(
id SERIAL PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS user(
  id SERIAL PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(255) UNIQUE NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  role_id BIGINT UNSIGNED,
  FOREIGN KEY(role_id) REFERENCES user_role(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS geographic_point(
  id SERIAL PRIMARY KEY ,
  latitude DOUBLE NOT NULL ,
  longitude DOUBLE NOT NULL ,
  address VARCHAR(1023) NOT NULL,
  CONSTRAINT UNIQUE (latitude, longitude)
);

CREATE TABLE IF NOT EXISTS vrp_instance(
    id SERIAL PRIMARY KEY,
    created_on TIMESTAMP NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    depot_id BIGINT UNSIGNED NOT NULL,
    preferred_departure_time TIMESTAMP NOT NULL,
    suggested_departure_time TIMESTAMP ,
    total_cost BIGINT UNSIGNED,
    FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE ,
    FOREIGN KEY(depot_id) REFERENCES geographic_point(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS delivery_point(
  geographic_point_id BIGINT UNSIGNED,
  vrp_instance_id BIGINT UNSIGNED,
  PRIMARY KEY (geographic_point_id, vrp_instance_id),
  FOREIGN KEY (geographic_point_id) REFERENCES geographic_point(id) ON DELETE CASCADE,
  FOREIGN KEY (vrp_instance_id) REFERENCES vrp_instance(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS driver(
  id SERIAL PRIMARY KEY ,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  phone VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  car VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS route(
  id SERIAL PRIMARY KEY ,
  driver_id BIGINT UNSIGNED NOT NULL,
  vrp_instance_id BIGINT UNSIGNED NOT NULL,
  cost BIGINT UNSIGNED,
  FOREIGN KEY (driver_id) REFERENCES driver(id) ON DELETE CASCADE,
  FOREIGN KEY (vrp_instance_id) REFERENCES vrp_instance(id) ON DELETE CASCADE,
  CONSTRAINT UNIQUE (driver_id, vrp_instance_id)
  );

CREATE TABLE IF NOT EXISTS geographic_point_to_route_assignment(
  geographic_point_id BIGINT UNSIGNED NOT NULL,
  route_id BIGINT UNSIGNED NOT NULL,
  index_in_route BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (geographic_point_id, route_id) ,
  FOREIGN KEY (geographic_point_id) REFERENCES geographic_point(id) ON DELETE CASCADE ,
  FOREIGN KEY (route_id) REFERENCES route(id) ON DELETE CASCADE
);