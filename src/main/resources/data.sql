-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id,username,password,authority) VALUES (1,'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS',1);

-- Ten player users, named player1 with passwor 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id,username,password,authority) VALUES (4,'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (5,'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (6,'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (7,'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (8,'player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (9,'player6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (10,'player7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (11,'player8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (12,'player9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (13,'player10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (14,'WMR4374','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (15,'MDP3345','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (16,'BPV2455','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (17,'MTD8580','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (18,'migsorrod1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (19,'marpadgom1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);
INSERT INTO appusers(id,username,password,authority) VALUES (20,'iestrada','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e',2);

INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (1, 'Miguel', 'Garcia', 'miguel@gmail.com', 15);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (2, 'Javi', 'Martos', 'javi@gmail.com', 16);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (3, 'Kiko', 'Garcia', 'ertrdf@gmail.com', 4);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (4, 'Josele', 'Cegri', 'jfjtrt@gmail.com', 14);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (5, 'Coquelin', 'Garcia', 'dfdgd@gmail.com', 5);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (6, 'Carlos', 'Galesa', 'asdfas@gmail.com', 17);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (7, 'Player', '3', 'player3@example.com', 6);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (8, 'Bedilia', 'Estrada Torres', 'iestrada@us.es', 20);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (9, 'Player', '4', 'player4@example.com', 7);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (10, 'Player', '5', 'player5@example.com', 8);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (11, 'Player', '6', 'player6@example.com', 9);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (12, 'Player', '7', 'player7@example.com', 10);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (13, 'Player', '8', 'player8@example.com', 11);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (14, 'Player', '9', 'player9@example.com', 12);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (15, 'Player', '10', 'player10@example.com', 13);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (16, 'Miguel', 'Soriano', 'migsorrod1@example.com', 18);
INSERT INTO players(id, first_name, last_name, email, user_id) VALUES (17, 'Marco', 'Padilla', 'marpadgom1@example.com', 19);


INSERT INTO matches(creator_id, current_active_player, current_player_turn, id, is_last_round, is_last_turn, round, finish_date, 
                        start_date, territory, criteriaA1, criteriaA2, criteriaB1, criteriaB2, dice, state)
                            VALUES (1, 1, 1, 1, true, true, 6,'2023-10-01 12:00:00', '2023-10-01 11:00:00', 'BOSQUE', 'UNO', 'DOS', 'TRES', 'CUATRO', 5, 'FINISHED');
INSERT INTO match_players(match_id, player_id) VALUES (1, 1), (1, 2);
INSERT INTO match_winners(match_id, player_id) VALUES (1, 1), (1, 2);

INSERT INTO matches(creator_id, current_active_player, current_player_turn, id, is_last_round, is_last_turn, round, finish_date, 
                        start_date, territory, criteriaA1, criteriaA2, criteriaB1, criteriaB2, dice, state)
                            VALUES (1, 1, 1, 2, true, true, 6,'2024-10-01 12:00:00', '2024-10-01 11:00:00', 'BOSQUE', 'UNO', 'DOS', 'TRES', 'CUATRO', 5, 'FINISHED');
INSERT INTO match_players(match_id, player_id) VALUES (2, 1), (2, 3);
INSERT INTO match_winners(match_id, player_id) VALUES (2, 1), (2, 3);

INSERT INTO matches(creator_id, current_active_player, current_player_turn, id, is_last_round, is_last_turn, round, finish_date, 
                        start_date, territory, criteriaA1, criteriaA2, criteriaB1, criteriaB2, dice, state)
                            VALUES (1, 1, 1, 3, true, true, 6,'2025-10-01 12:00:00', '2025-10-01 11:00:00', 'BOSQUE', 'UNO', 'DOS', 'TRES', 'CUATRO', 5, 'FINISHED');
INSERT INTO match_players(match_id, player_id) VALUES (3, 1), (3, 4);
INSERT INTO match_winners(match_id, player_id) VALUES (3, 1), (3, 4);