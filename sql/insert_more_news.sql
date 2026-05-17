-- Additional health articles for HealthPals.
-- Run on an existing DB: mysql -u root -p personal_health < sql/insert_more_news.sql

INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES
(13, 'Is 10,000 Steps a Day Right for You?',
'<p style=\"text-align: justify;\">Ten thousand steps became a popular goal because it is easy to remember—not because it is a medical rule for everyone. Movement matters, but the best target depends on your age, fitness, and any health conditions.</p>
<h3 style=\"text-align: justify;\"><strong>What research suggests</strong></h3>
<p style=\"text-align: justify;\">Studies link more daily steps with lower risk of heart disease and better mood. Benefits often appear well below 10,000 steps for people who were previously sedentary. Adding 2,000–3,000 steps per day can be a realistic first goal.</p>
<h3 style=\"text-align: justify;\"><strong>Make it sustainable</strong></h3>
<ul style=\"text-align: justify;\">
<li>Break walks into 10-minute blocks after meals.</li>
<li>Use stairs, park farther away, or walk meetings when possible.</li>
<li>Track trends weekly rather than chasing a perfect daily number.</li>
</ul>
<p style=\"text-align: justify;\">If you have joint pain, heart symptoms, or balance concerns, ask a clinician before sharply increasing activity.</p>',
3, 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-03-12 09:15:00'),

(14, 'A Simple Weekly Plan to Manage Stress',
'<p style=\"text-align: justify;\">Stress is not only in your head—it affects sleep, appetite, blood pressure, and focus. You cannot remove every stressor, but you can build habits that help your nervous system recover.</p>
<h3 style=\"text-align: justify;\"><strong>Three anchors that help most people</strong></h3>
<ul style=\"text-align: justify;\">
<li><strong>Sleep window:</strong> keep a consistent wake time, even on weekends.</li>
<li><strong>Movement:</strong> 20–30 minutes of walking most days.</li>
<li><strong>Connection:</strong> one meaningful conversation per week counts.</li>
</ul>
<h3 style=\"text-align: justify;\"><strong>When to seek support</strong></h3>
<p style=\"text-align: justify;\">Talk to a mental health professional if anxiety or low mood lasts more than two weeks, interferes with work or relationships, or includes thoughts of self-harm. Crisis lines are available 24/7 in many regions.</p>',
1, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 1, '2025-04-02 14:20:00'),

(15, 'Heart-Healthy Habits You Can Start This Week',
'<p style=\"text-align: justify;\">Cardiovascular health is shaped by daily choices: what you eat, how you move, whether you smoke, and how you manage stress. Small changes compound over years.</p>
<h3 style=\"text-align: justify;\"><strong>Evidence-based starting points</strong></h3>
<ul style=\"text-align: justify;\">
<li>Choose unsaturated fats (olive oil, nuts, fish) over trans fats when possible.</li>
<li>Limit added salt in packaged foods and restaurant meals.</li>
<li>Aim for 150 minutes of moderate activity per week.</li>
<li>Know your blood pressure and discuss results with a clinician.</li>
</ul>
<p style=\"text-align: justify;\">Chest pain, sudden shortness of breath, or pain radiating to the arm or jaw need urgent medical evaluation—do not wait.</p>',
5, '/img/articles/heart-healthy.jpg', NULL, 1, '2025-04-18 11:00:00'),

(16, 'Screen Time at Night: How It Affects Sleep',
'<p style=\"text-align: justify;\">Phones and laptops before bed can delay sleep by suppressing melatonin and keeping your brain in alert mode. The issue is not only screen brightness but also stimulating content.</p>
<h3 style=\"text-align: justify;\"><strong>Practical evening routine</strong></h3>
<ul style=\"text-align: justify;\">
<li>Stop intense work or social scrolling 60 minutes before bed.</li>
<li>Use night mode and lower brightness if you must use a device.</li>
<li>Keep the bedroom cool, dark, and quiet.</li>
<li>Wake at the same time daily to stabilize your body clock.</li>
</ul>
<p style=\"text-align: justify;\">Persistent insomnia for more than a month may benefit from cognitive behavioral therapy for insomnia (CBT-I), which is often more effective long term than medication alone.</p>',
2, '/img/articles/screen-sleep.jpg', NULL, 0, '2025-05-01 20:45:00'),

(17, 'Strength Training for Beginners: Where to Start',
'<p style=\"text-align: justify;\">Strength training protects muscle and bone as you age, supports metabolism, and can improve balance. You do not need a gym membership to begin.</p>
<h3 style=\"text-align: justify;\"><strong>A simple twice-weekly template</strong></h3>
<ul style=\"text-align: justify;\">
<li>Squats or sit-to-stand from a chair: 2 sets of 8–12 reps.</li>
<li>Wall push-ups or incline push-ups: 2 sets of 8–12 reps.</li>
<li>Row with resistance band or backpack: 2 sets of 8–12 reps.</li>
<li>Plank or dead bug core exercise: 2 sets of 20–30 seconds.</li>
</ul>
<p style=\"text-align: justify;\">Rest at least one day between sessions. Stop if you feel sharp pain, dizziness, or chest discomfort.</p>',
3, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-05-20 08:30:00'),

(18, 'Hydration: How Much Water Do You Really Need?',
'<p style=\"text-align: justify;\">Hydration needs vary with climate, activity, body size, and health conditions. Thirst is a useful guide for most healthy adults.</p>
<h3 style=\"text-align: justify;\"><strong>Helpful habits</strong></h3>
<ul style=\"text-align: justify;\">
<li>Drink water with meals and during exercise.</li>
<li>Check urine color: pale yellow usually means adequate hydration.</li>
<li>Increase fluids in heat, fever, or diarrhea—oral rehydration solutions may help.</li>
</ul>
<p style=\"text-align: justify;\">People with heart failure or kidney disease should follow fluid limits set by their care team. Drinking excessive water without medical reason is not healthier.</p>',
2, '/img/articles/hydration.jpg', NULL, 0, '2025-06-08 16:10:00'),

(19, 'Five-Minute Breathing Breaks That Calm the Nervous System',
'<p style=\"text-align: justify;\">Slow breathing can lower heart rate and reduce perceived stress within minutes. It is a tool you can use before meetings, exams, or difficult conversations.</p>
<h3 style=\"text-align: justify;\"><strong>Try box breathing</strong></h3>
<p style=\"text-align: justify;\">Inhale through the nose for 4 seconds, hold 4 seconds, exhale 4 seconds, hold 4 seconds. Repeat for 4–6 cycles. Sit upright, shoulders relaxed, eyes soft or closed.</p>
<p style=\"text-align: justify;\">Breathing exercises complement—but do not replace—therapy or medical care for panic disorder, PTSD, or severe anxiety.</p>',
1, 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-06-22 07:50:00'),

(20, 'Seasonal Allergies and Staying Active Outdoors',
'<p style=\"text-align: justify;\">Pollen seasons can make outdoor exercise uncomfortable. Planning around triggers helps you keep moving without unnecessary suffering.</p>
<h3 style=\"text-align: justify;\"><strong>Tips that often help</strong></h3>
<ul style=\"text-align: justify;\">
<li>Check local pollen forecasts and exercise after rain when counts drop.</li>
<li>Shower and change clothes after outdoor activity.</li>
<li>Use saline nasal rinse as directed by a pharmacist or clinician.</li>
<li>Consider indoor alternatives on high-pollen days.</li>
</ul>
<p style=\"text-align: justify;\">See a clinician if symptoms include wheezing, facial pain, or fever—they may indicate asthma or infection rather than simple allergies.</p>',
6, '/img/articles/allergies.jpg', NULL, 0, '2025-07-05 13:25:00'),

(21, 'Desk Stretches for People Who Sit All Day',
'<p style=\"text-align: justify;\">Long sitting tightens hips, rounds the shoulders, and can contribute to neck and lower-back discomfort. Short movement breaks reduce stiffness more than one long stretch at night.</p>
<h3 style=\"text-align: justify;\"><strong>Every hour, try</strong></h3>
<ul style=\"text-align: justify;\">
<li>Stand and walk for 2–3 minutes.</li>
<li>Neck side bends and shoulder rolls: 30 seconds each.</li>
<li>Hip flexor stretch in a lunge position: 20 seconds per side.</li>
<li>Wrist circles if you type frequently.</li>
</ul>
<p style=\"text-align: justify;\">Ergonomic setup matters: screen at eye level, feet flat, elbows near 90 degrees.</p>',
7, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-08-14 10:40:00'),

(22, 'Why Social Connection Matters for Health',
'<p style=\"text-align: justify;\">Loneliness and weak social ties are linked to higher risks of depression, heart disease, and early mortality. Connection is a health behavior, not a luxury.</p>
<h3 style=\"text-align: justify;\"><strong>Small steps that count</strong></h3>
<ul style=\"text-align: justify;\">
<li>Schedule a regular call or walk with one friend.</li>
<li>Join a class, volunteer group, or community activity aligned with your interests.</li>
<li>Be specific when asking for help—it makes it easier for others to respond.</li>
</ul>
<p style=\"text-align: justify;\">If social anxiety or depression makes connection hard, professional support can help you rebuild skills at a manageable pace.</p>',
8, 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-09-01 18:00:00'),

(23, 'Understanding Blood Sugar Spikes After Meals',
'<p style=\"text-align: justify;\">After eating, blood glucose rises and insulin helps cells use that energy. Large spikes followed by crashes can leave you tired and hungry again soon.</p>
<h3 style=\"text-align: justify;\"><strong>Steadier glucose strategies</strong></h3>
<ul style=\"text-align: justify;\">
<li>Pair carbohydrates with protein, fiber, or healthy fat.</li>
<li>Choose whole grains over refined flour when practical.</li>
<li>Take a short walk after meals when you can.</li>
</ul>
<p style=\"text-align: justify;\">People with diabetes should follow individualized plans from their care team rather than general articles alone.</p>',
5, 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-10-10 12:15:00'),

(24, 'Morning Sunlight and Your Body Clock',
'<p style=\"text-align: justify;\">Morning light helps set circadian rhythm, which influences sleep timing, alertness, and hormone patterns. Even cloudy outdoor light is often brighter than indoor lighting.</p>
<h3 style=\"text-align: justify;\"><strong>Easy routine</strong></h3>
<p style=\"text-align: justify;\">Within an hour of waking, spend 10–20 minutes outside or by a bright window. Combine with a short walk if possible. In winter or shift work, discuss light therapy devices with a clinician.</p>',
2, 'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-11-02 07:30:00');
