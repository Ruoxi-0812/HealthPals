-- Featured section: exactly 3 articles, unique covers, richer body text.
-- mysql -h 127.0.0.1 -P 3306 -u root -p personal_health < sql/fix_featured_news.sql

UPDATE `news` SET `is_top` = 0;

UPDATE `news` SET `is_top` = 1 WHERE `id` IN (12, 14, 15);

-- Unique covers (avoid duplicate doctor/laptop image on id 4 vs 15)
UPDATE `news` SET `cover` = '/img/articles/principles-health.jpg' WHERE `id` = 4;
UPDATE `news` SET `cover` = '/img/articles/diet-cells.jpg' WHERE `id` = 12;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 14;
UPDATE `news` SET `cover` = '/img/articles/heart-healthy.jpg' WHERE `id` = 15;

UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 2;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 6;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 7;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 8;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 9;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 10;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 11;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 13;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1520206183501-b80d53754c1e?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 16;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 17;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1548839140-29a7492991ca?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 18;
UPDATE `news` SET `cover` = 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80' WHERE `id` = 19;

UPDATE `news` SET `content` = '<p style=\"text-align: justify;\">Preventive care works best when you notice small changes early. Regular checkups, vaccines, and screening tests are not only for people who already feel unwell—they help you stay ahead of problems.</p>
<h3 style=\"text-align: justify;\"><strong>What prevention includes</strong></h3>
<ul style=\"text-align: justify;\">
<li>Annual physical exams and age-appropriate screenings (blood pressure, cholesterol, cancer screens).</li>
<li>Staying current on recommended vaccines.</li>
<li>Discussing family history with your clinician to personalize risk.</li>
</ul>
<h3 style=\"text-align: justify;\"><strong>Everyday habits that support immunity</strong></h3>
<p style=\"text-align: justify;\">Sleep, balanced nutrition, hand hygiene, and stress management all influence how your body responds to infection. None of these replace medical care, but together they build resilience.</p>
<p style=\"text-align: justify;\">Seek care promptly for persistent fever, unexplained weight loss, chest pain, or symptoms that worsen over several days.</p>'
WHERE `id` = 2;

UPDATE `news` SET `content` = CONCAT(`content`, '
<h3 style=\"text-align: justify;\"><strong>Meal planning without overwhelm</strong></h3>
<p style=\"text-align: justify;\">Cook once, eat twice: roast a tray of vegetables and a protein on Sunday, then mix into bowls, wraps, or salads through the week. Keep frozen fruit and greens on hand for quick smoothies when time is tight.</p>
<h3 style=\"text-align: justify;\"><strong>Reading labels</strong></h3>
<p style=\"text-align: justify;\">Compare products by fiber, added sugar, and sodium per serving—not marketing claims on the front of the package. Small swaps (whole-grain bread, plain yogurt with fruit) add up over months.</p>
<p style=\"text-align: justify;\">If you manage diabetes, kidney disease, or food allergies, work with a registered dietitian for a plan tailored to your labs and medications.</p>')
WHERE `id` = 12;

UPDATE `news` SET `content` = CONCAT(`content`, '
<h3 style=\"text-align: justify;\"><strong>Quick reset during a busy day</strong></h3>
<p style=\"text-align: justify;\">Set a phone reminder for a two-minute pause: unclench your jaw, drop your shoulders, and take five slow breaths. Short resets prevent stress from stacking unnoticed until evening.</p>
<h3 style=\"text-align: justify;\"><strong>Protecting recovery time</strong></h3>
<p style=\"text-align: justify;\">Boundaries around work email after hours and saying no to nonessential commitments are health choices. Sleep and social connection are not rewards for finishing everything—they are part of staying well.</p>')
WHERE `id` = 14;

UPDATE `news` SET `content` = CONCAT(`content`, '
<h3 style=\"text-align: justify;\"><strong>Understanding your numbers</strong></h3>
<p style=\"text-align: justify;\">Blood pressure, LDL cholesterol, blood sugar, and waist circumference each tell part of the story. Track trends over time rather than obsessing over a single reading after a stressful day.</p>
<h3 style=\"text-align: justify;\"><strong>Building a support team</strong></h3>
<p style=\"text-align: justify;\">A primary care clinician, pharmacist, and trusted family member can help you remember medications and lifestyle goals. Share your home blood pressure log at visits so adjustments are data-driven.</p>')
WHERE `id` = 15;

UPDATE `news` SET `content` = REPLACE(`content`, '<p><img src=\"https://p3-sign.toutiaoimg.com/pgc-image/SUnEYkXADWQXzc~noop.image?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1721543649&amp;x-signature=RuB5B1ApqxK6xu2fLl%2Bvs%2Bt4m%2BM%3D\" alt=\"\" data-href=\"\" style=\"height: auto;\"></p>', '')
WHERE `id` = 5 AND `content` LIKE '%toutiaoimg%';
