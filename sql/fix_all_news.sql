-- Fix all news: unique working covers, full article bodies, 3 featured.
-- mysql -h 127.0.0.1 -P 3306 -u root -p personal_health < sql/fix_all_news.sql

UPDATE `news` SET `is_top` = 0;
UPDATE `news` SET `is_top` = 1 WHERE `id` IN (12, 14, 15);

-- Verified Unsplash URLs (HTTP 200); one unique image per article
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 1;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 2;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 3;
UPDATE `news` SET `cover` = '/img/articles/principles-health.jpg' WHERE `id` = 4;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1628595351029-c2bf17511435?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 5;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 6;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 7;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 8;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 9;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 10;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 11;
UPDATE `news` SET `cover` = '/img/articles/diet-cells.jpg' WHERE `id` = 12;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 13;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 14;
UPDATE `news` SET `cover` = '/img/articles/heart-healthy.jpg' WHERE `id` = 15;
UPDATE `news` SET `cover` = '/img/articles/screen-sleep.jpg' WHERE `id` = 16;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 17;
UPDATE `news` SET `cover` = '/img/articles/hydration.jpg' WHERE `id` = 18;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 19;
UPDATE `news` SET `cover` = '/img/articles/allergies.jpg' WHERE `id` = 20;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 21;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 22;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 23;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 24;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Preventive care works best when you notice small changes early. Regular checkups, vaccines, and screening tests help you stay ahead of problems—not only after symptoms appear.</p>
<h3 style=\"text-align: justify;\"><strong>What prevention includes</strong></h3>
<ul style=\"text-align: justify;\"><li>Annual physical exams and age-appropriate screenings.</li><li>Staying current on recommended vaccines.</li><li>Sharing family history with your clinician.</li></ul>
<h3 style=\"text-align: justify;\"><strong>Everyday habits</strong></h3>
<p style=\"text-align: justify;\">Sleep, balanced nutrition, hand hygiene, and stress management support immunity. Seek care for persistent fever, unexplained weight loss, or chest pain.</p>'
WHERE `id` = 2;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Modern schedules often push sleep, meals, and movement to the margins. Over months, that pattern raises the risk of burnout, weight gain, and higher blood pressure.</p>
<h3 style=\"text-align: justify;\"><strong>Rebuild rhythm</strong></h3>
<ul style=\"text-align: justify;\"><li>Block 10-minute breaks between meetings.</li><li>Prepare simple meals ahead for busy evenings.</li><li>Set a consistent bedtime alarm, not only a wake alarm.</li></ul>
<p style=\"text-align: justify;\">Small, repeatable habits outperform occasional intense health kicks.</p>'
WHERE `id` = 7;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Many people seek care only when symptoms become impossible to ignore. Routine monitoring catches issues earlier, when treatment is often simpler.</p>
<h3 style=\"text-align: justify;\"><strong>Track what matters</strong></h3>
<ul style=\"text-align: justify;\"><li>Blood pressure at home if your clinician recommends it.</li><li>Annual labs and dental visits.</li><li>Mental health check-ins during high-stress seasons.</li></ul>
<p style=\"text-align: justify;\">Use your HealthPals records to spot trends and discuss them at appointments.</p>'
WHERE `id` = 8;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Chronic conditions such as diabetes, hypertension, and arthritis develop gradually. Early lifestyle changes and medication adherence can slow progression significantly.</p>
<h3 style=\"text-align: justify;\"><strong>Management pillars</strong></h3>
<ul style=\"text-align: justify;\"><li>Take prescribed medicines on schedule.</li><li>Follow nutrition guidance tailored to your condition.</li><li>Stay active within your ability and safety limits.</li></ul>
<p style=\"text-align: justify;\">Work with your care team to set realistic targets rather than comparing yourself to others.</p>'
WHERE `id` = 9;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">A sustainable healthy lifestyle is built from sleep, movement, nutrition, and social connection—not from extreme short-term diets.</p>
<h3 style=\"text-align: justify;\"><strong>Start with one change</strong></h3>
<ul style=\"text-align: justify;\"><li>Add a daily 15-minute walk.</li><li>Swap one sugary drink for water or unsweetened tea.</li><li>Prepare a consistent sleep window.</li></ul>
<p style=\"text-align: justify;\">After three weeks, add a second habit. Consistency beats intensity.</p>'
WHERE `id` = 10;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Health is easier to maintain when people around you share similar goals. Families and friends can encourage checkups, cooking at home, and active weekends together.</p>
<h3 style=\"text-align: justify;\"><strong>Ideas to try together</strong></h3>
<ul style=\"text-align: justify;\"><li>Weekly walks or bike rides.</li><li>Cooking one new vegetable-based meal per week.</li><li>Reminding each other about screenings and vaccines.</li></ul>
<p style=\"text-align: justify;\">Support works both ways—ask what helps others, and share what you need.</p>'
WHERE `id` = 11;

UPDATE `news` SET `content` = REPLACE(`content`, '<p><img src=\"https://p3-sign.toutiaoimg.com/pgc-image/SUnEYkXADWQXzc~noop.image?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1721543649&amp;x-signature=RuB5B1ApqxK6xu2fLl%2Bvs%2Bt4m%2BM%3D\" alt=\"\" data-href=\"\" style=\"height: auto;\"></p>', '')
WHERE `id` = 5 AND `content` LIKE '%toutiaoimg%';

UPDATE `news` SET `content` = CONCAT(`content`, '<h3 style=\"text-align: justify;\"><strong>Wind-down checklist</strong></h3><p style=\"text-align: justify;\">Dim lights, charge devices outside the bedroom when possible, and avoid heavy meals within two hours of bed. If you still cannot sleep after 20 minutes, get up and read something calm until drowsy returns.</p>')
WHERE `id` = 16 AND `content` NOT LIKE '%Wind-down checklist%';

UPDATE `news` SET `content` = CONCAT(`content`, '<h3 style=\"text-align: justify;\"><strong>Signs you may need more fluids</strong></h3><p style=\"text-align: justify;\">Headache, dark urine, dizziness after heat exposure, or dry mouth can signal dehydration. Older adults may feel less thirsty but still need regular fluids.</p>')
WHERE `id` = 18 AND `content` NOT LIKE '%Signs you may need more fluids%';
