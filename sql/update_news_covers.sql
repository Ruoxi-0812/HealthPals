-- Article & model covers from Unsplash (theme-matched). Requires network from users' browsers.
-- personal_health DB. mysql -u root -p personal_health < sql/update_news_covers.sql

UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 1;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 2;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 3;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 4;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1628595351029-c2bf17511435?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 5;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 6;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 7;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 8;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 9;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 10;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 11;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 12;

UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 1;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 2;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 4;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1505576399279-565b52d4ac71?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 7;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 8;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 9;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 10;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 11;
UPDATE `health_model_config` SET `cover` = 'https://images.unsplash.com/photo-1628595351029-c2bf17511435?auto=format&fit=crop&w=700&h=525&q=80' WHERE `id` = 12;

UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 1;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 3;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 8;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 9;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 10;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 11;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 12;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 13;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 14;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 15;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 16;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 17;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 18;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 19;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 20;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 21;
UPDATE `user` SET `user_avatar` = 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2' WHERE `id` = 22;
