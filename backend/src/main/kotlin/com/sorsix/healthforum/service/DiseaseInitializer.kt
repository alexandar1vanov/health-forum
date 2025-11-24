package com.sorsix.healthforum.service

import com.sorsix.healthforum.model.Disease
import com.sorsix.healthforum.model.enumerations.DiseaseCategory
import com.sorsix.healthforum.repository.DiseaseRepository
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DiseaseInitializer(
    private val diseaseRepository: DiseaseRepository
) {
    @PostConstruct
    @Transactional
    fun initializeDiseases() {
        if (diseaseRepository.count() == 0L) {
            val diseases = listOf(
                Disease(
                    name = "Common Cold",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "This mild viral infection primarily affects the nose and throat, causing symptoms like runny nose, sneezing, and sore throat. It usually resolves on its own within a week or two."
                ),
                Disease(
                    name = "Influenza (Flu)",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "A contagious respiratory illness caused by influenza viruses, the flu can cause fever, cough, sore throat, muscle aches, and fatigue. It can range from mild to severe and sometimes lead to complications."
                ),
                Disease(
                    name = "Pneumonia",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "An infection that inflames the air sacs in one or both lungs, pneumonia can be caused by bacteria, viruses, or fungi. This inflammation leads to the air sacs filling with fluid or pus, causing cough, fever, and difficulty breathing."
                ),
                Disease(
                    name = "Bronchitis (Acute)",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "This occurs when the airways of the lungs (bronchial tubes) become inflamed, often after a viral infection. It is characterized by a persistent cough, which may produce mucus."
                ),
                Disease(
                    name = "Sinusitis",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "An inflammation or swelling of the tissue lining the sinuses, which are air-filled cavities in the skull. This often results from an infection, causing facial pain, pressure, and nasal congestion."
                ),
                Disease(
                    name = "Strep Throat",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "A bacterial infection of the throat and tonsils caused by Streptococcus pyogenes. It is characterized by a sudden sore throat, pain when swallowing, and sometimes fever and red spots on the roof of the mouth."
                ),
                Disease(
                    name = "Tuberculosis (TB)",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "A potentially serious infectious disease that mainly affects the lungs but can also spread to other parts of the body. It is caused by a bacterium called Mycobacterium tuberculosis and can be latent or active."
                ),
                Disease(
                    name = "COVID-19",
                    category = DiseaseCategory.INFECTIOUS_RESPIRATORY,
                    description = "A respiratory illness caused by the SARS-CoV-2 virus that can range from mild to severe. Common symptoms include fever, cough, fatigue, and loss of taste or smell, and it can sometimes lead to serious complications."
                ),
                Disease(
                    name = "Norovirus Infection",
                    category = DiseaseCategory.INFECTIOUS_GASTROINTESTINAL,
                    description = "A highly contagious virus that causes inflammation of the stomach and intestines (gastroenteritis). It leads to symptoms like nausea, vomiting, diarrhea, and stomach cramps, usually lasting for a few days."
                ),
                Disease(
                    name = "Rotavirus Infection",
                    category = DiseaseCategory.INFECTIOUS_GASTROINTESTINAL,
                    description = "A common cause of severe diarrhea in infants and young children. It often presents with vomiting, fever, and abdominal pain, and can lead to dehydration."
                ),
                Disease(
                    name = "Food Poisoning",
                    category = DiseaseCategory.INFECTIOUS_GASTROINTESTINAL,
                    description = "Illness caused by consuming contaminated food or drink containing harmful bacteria, viruses, parasites, or toxins. Symptoms vary depending on the cause but often include nausea, vomiting, diarrhea, and abdominal cramps."
                ),
                Disease(
                    name = "Gastroenteritis",
                    category = DiseaseCategory.INFECTIOUS_GASTROINTESTINAL,
                    description = "Inflammation of the lining of the stomach and small intestine, often caused by a viral or bacterial infection. It typically results in symptoms such as vomiting, diarrhea, abdominal cramps, and sometimes fever."
                ),
                Disease(
                    name = "Helicobacter pylori infection",
                    category = DiseaseCategory.INFECTIOUS_GASTROINTESTINAL,
                    description = "A common bacterium that infects the lining of the stomach. While many people have no symptoms, it can lead to stomach ulcers, gastritis, and an increased risk of stomach cancer."
                ),
                Disease(
                    name = "Urinary Tract Infections (UTIs)",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "Infections involving any part of the urinary system, including the bladder, urethra, ureters, and kidneys. They commonly cause symptoms like painful urination, frequent urge to urinate, and cloudy urine."
                ),
                Disease(
                    name = "Skin Infections (Cellulitis, Impetigo)",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "Cellulitis is a bacterial infection of the deep layers of the skin and subcutaneous tissue, causing redness, swelling, and pain. Impetigo is a superficial bacterial skin infection characterized by red sores that quickly rupture and form a yellowish crust."
                ),
                Disease(
                    name = "Conjunctivitis (Pinkeye)",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "An inflammation or infection of the transparent membrane (conjunctiva) that lines the eyelid and covers the white part of the eyeball. It can be caused by viruses, bacteria, allergies, or irritants, leading to redness, itching, and discharge."
                ),
                Disease(
                    name = "Herpes Simplex",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "A viral infection caused by the herpes simplex virus (HSV), resulting in sores, typically around the mouth (oral herpes) or genitals (genital herpes). These infections are recurrent and can be triggered by various factors."
                ),
                Disease(
                    name = "Chickenpox and Shingles",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "Chickenpox is a highly contagious viral infection caused by the varicella-zoster virus (VZV), characterized by an itchy, blister-like rash all over the body. Shingles is a reactivation of the same virus later in life, causing a painful rash with blisters in a localized area."
                ),
                Disease(
                    name = "HIV/AIDS",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "Human Immunodeficiency Virus (HIV) attacks the body's immune system, and if left untreated, it can lead to Acquired Immunodeficiency Syndrome (AIDS). AIDS is the late stage of HIV infection when the immune system is severely damaged, making individuals susceptible to opportunistic infections and cancers."
                ),
                Disease(
                    name = "Malaria",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "A serious and sometimes fatal disease transmitted by infected female Anopheles mosquitoes. It is caused by Plasmodium parasites that multiply in the liver and then infect red blood cells, leading to fever, chills, and flu-like illness."
                ),
                Disease(
                    name = "Dengue Fever",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "A mosquito-borne viral infection that can cause a severe flu-like illness. Symptoms include high fever, severe headache, pain behind the eyes, muscle and joint pain, nausea, vomiting, and rash."
                ),
                Disease(
                    name = "Measles",
                    category = DiseaseCategory.INFECTIOUS_OTHER,
                    description = "A highly contagious viral infection that spreads through the air by respiratory droplets. It is characterized by a fever, runny nose, cough, tiny white spots inside the mouth, and a characteristic full-body rash."
                ),
                Disease(
                    name = "Hypertension (High Blood Pressure)",
                    category = DiseaseCategory.CARDIOVASCULAR,
                    description = "A condition in which the force of the blood pushing against the artery walls is consistently too high. If left untreated, it can increase the risk of heart disease, stroke, and kidney problems."
                ),
                Disease(
                    name = "Coronary Artery Disease (CAD)",
                    category = DiseaseCategory.CARDIOVASCULAR,
                    description = "A condition in which the arteries that supply blood to the heart become narrowed or blocked, usually due to the buildup of plaque. This can lead to chest pain (angina), shortness of breath, heart attack, and other heart problems."
                ),
                Disease(
                    name = "Stroke",
                    category = DiseaseCategory.CARDIOVASCULAR,
                    description = "Occurs when the blood supply to a part of the brain is interrupted or severely reduced, depriving brain tissue of oxygen and nutrients. This can cause brain cells to die within minutes, leading to lasting brain damage, disability, or death."
                ),
                Disease(
                    name = "Heart Failure",
                    category = DiseaseCategory.CARDIOVASCULAR,
                    description = "A chronic, progressive condition in which the heart muscle is unable to pump enough blood to meet the body's needs. This can result from various underlying heart conditions and leads to symptoms like shortness of breath, fatigue, and swelling in the legs and ankles."
                ),
                Disease(
                    name = "Arrhythmias",
                    category = DiseaseCategory.CARDIOVASCULAR,
                    description = "Irregular heartbeats caused by problems with the electrical signals that control the heart's rhythm. They can range from harmless to life-threatening, causing symptoms like palpitations, dizziness, or fainting."
                ),
                Disease(
                    name = "Type 2 Diabetes Mellitus",
                    category = DiseaseCategory.METABOLIC,
                    description = "A chronic metabolic disorder characterized by high blood sugar (glucose) levels due to insulin resistance and relative insulin deficiency. The body's cells don't respond effectively to insulin, and the pancreas may not produce enough insulin to overcome this resistance."
                ),
                Disease(
                    name = "Type 1 Diabetes Mellitus",
                    category = DiseaseCategory.METABOLIC,
                    description = "An autoimmune disease in which the body's immune system attacks and destroys the insulin-producing beta cells in the pancreas. This leads to an absolute deficiency of insulin, requiring lifelong insulin therapy to regulate blood sugar levels."
                ),
                Disease(
                    name = "Obesity",
                    category = DiseaseCategory.METABOLIC,
                    description = "A complex condition characterized by excessive accumulation of body fat that may impair health. It is typically defined by a body mass index (BMI) of 30 or higher and increases the risk of various health problems."
                ),
                Disease(
                    name = "Hyperlipidemia (High Cholesterol)",
                    category = DiseaseCategory.METABOLIC,
                    description = "A condition characterized by high levels of lipids (fats), such as cholesterol and triglycerides, in the blood. Elevated cholesterol can contribute to the buildup of plaque in the arteries, increasing the risk of heart disease and stroke."
                ),
                Disease(
                    name = "Asthma",
                    category = DiseaseCategory.CHRONIC_RESPIRATORY,
                    description = "A chronic inflammatory disease of the airways that makes breathing difficult. It is characterized by episodes of wheezing, shortness of breath, chest tightness, and coughing, often triggered by allergens, irritants, or exercise."
                ),
                Disease(
                    name = "Chronic Obstructive Pulmonary Disease (COPD)",
                    category = DiseaseCategory.CHRONIC_RESPIRATORY,
                    description = "A progressive lung disease that makes it difficult to breathe. It primarily includes emphysema and chronic bronchitis, causing airflow obstruction that worsens over time."
                ),
                Disease(
                    name = "Allergic Rhinitis (Hay Fever)",
                    category = DiseaseCategory.CHRONIC_RESPIRATORY,
                    description = "An inflammation of the nasal passages caused by an allergic reaction to airborne allergens such as pollen, dust mites, or pet dander. Symptoms include sneezing, runny nose, nasal congestion, and itchy eyes."
                ),
                Disease(
                    name = "Osteoarthritis",
                    category = DiseaseCategory.MUSCULOSKELETAL,
                    description = "A degenerative joint disease characterized by the breakdown of cartilage, the protective tissue that cushions the ends of bones in a joint. This can cause pain, stiffness, and swelling, commonly affecting the hips, knees, and hands."
                ),
                Disease(
                    name = "Rheumatoid Arthritis",
                    category = DiseaseCategory.MUSCULOSKELETAL,
                    description = "A chronic autoimmune disease in which the body's immune system mistakenly attacks the lining of the joints (synovium). This causes inflammation that can lead to pain, swelling, stiffness, and eventually joint damage and deformity."
                ),
                Disease(
                    name = "Back Pain",
                    category = DiseaseCategory.MUSCULOSKELETAL,
                    description = "A common condition that can result from various issues affecting the muscles, nerves, bones, and discs in the back. It can range from a dull ache to sharp, debilitating pain and can be acute or chronic."
                ),
                Disease(
                    name = "Osteoporosis",
                    category = DiseaseCategory.MUSCULOSKELETAL,
                    description = "A condition characterized by a decrease in bone density and strength, making bones brittle and more prone to fractures. It often develops silently over many years, and fractures may be the first sign."
                ),
                Disease(
                    name = "Depression",
                    category = DiseaseCategory.MENTAL_HEALTH,
                    description = "A common and serious mood disorder that negatively affects how you feel, think, and act. It is characterized by persistent sadness, loss of interest or pleasure, fatigue, and other physical and psychological symptoms."
                ),
                Disease(
                    name = "Anxiety Disorders",
                    category = DiseaseCategory.MENTAL_HEALTH,
                    description = "A group of mental health conditions characterized by excessive worry, fear, nervousness, or apprehension. Different types include generalized anxiety disorder, panic disorder, social anxiety disorder, and specific phobias."
                ),
                Disease(
                    name = "Migraine and Tension Headaches",
                    category = DiseaseCategory.NEUROLOGICAL,
                    description = "Migraines are severe headaches often accompanied by throbbing pain, nausea, vomiting, and sensitivity to light and sound. Tension headaches are the most common type, typically presenting as a dull, aching pain or pressure across the forehead, sides, or back of the head."
                ),
                Disease(
                    name = "Alzheimer's Disease",
                    category = DiseaseCategory.NEUROLOGICAL,
                    description = "A progressive neurodegenerative disease that gradually destroys memory and thinking skills, and eventually the ability to carry out the simplest tasks. It is the most common cause of dementia among older adults."
                ),
                Disease(
                    name = "Lung Cancer",
                    category = DiseaseCategory.CANCER,
                    description = "A type of cancer that begins in the lungs, often in the cells lining the air passages. It is a leading cause of cancer death worldwide, with smoking being the primary risk factor."
                ),
                Disease(
                    name = "Breast Cancer",
                    category = DiseaseCategory.CANCER,
                    description = "Cancer that forms in the cells of the breasts. It is the most common cancer diagnosed in women worldwide and can occur in both men and women."
                ),
                Disease(
                    name = "Colorectal Cancer",
                    category = DiseaseCategory.CANCER,
                    description = "Cancer that starts in the colon or rectum (parts of the large intestine). It often begins as small, benign clumps of cells called polyps that can develop into cancer over time."
                ),
                Disease(
                    name = "Prostate Cancer",
                    category = DiseaseCategory.CANCER,
                    description = "Cancer that occurs in the prostate, a small walnut-shaped gland in males that produces seminal fluid. It is one of the most common types of cancer in men, and often grows slowly."
                ),
                Disease(
                    name = "Skin Cancer",
                    category = DiseaseCategory.CANCER,
                    description = "Cancer that begins in the skin, the body's largest organ. The main types include basal cell carcinoma, squamous cell carcinoma, and melanoma, with sun exposure being a major risk factor."
                ),
                Disease(
                    name = "Thyroid Disorders",
                    category = DiseaseCategory.AUTOIMMUNE,
                    description = "A group of conditions that affect the thyroid gland, a small butterfly-shaped gland in the neck that produces hormones regulating metabolism. These disorders can result in the thyroid producing too much (hyperthyroidism) or too little (hypothyroidism) thyroid hormone."
                ),
                Disease(
                    name = "Allergies (Food, Environmental)",
                    category = DiseaseCategory.ALLERGY,
                    description = "Hypersensitivity reactions of the immune system to normally harmless substances called allergens. Exposure to these allergens can trigger a range of symptoms, from mild skin rashes and sneezing to severe, life-threatening anaphylaxis."
                )
            );
            diseaseRepository.saveAll(diseases);
        }
    }
}