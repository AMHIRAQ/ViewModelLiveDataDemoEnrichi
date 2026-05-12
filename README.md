# ViewModelLiveDataDemoEnrichi

> Projet Android Java — Lab complet ViewModel + LiveData (Jetpack)  
> Basé sur le guide du cours · API 24+ · Material 3


---


https://github.com/user-attachments/assets/e059547a-d6e4-496c-8558-51a86022d115


---

## Structure du projet

```
app/
├── build.gradle
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/viewmodellivedatademoenrichi/
    │   ├── MainActivity.java          ← View (MVVM) : observe le ViewModel
    │   └── CounterViewModel.java      ← ViewModel : logique + LiveData + SavedState
    └── res/
        ├── drawable/
        │   └── bg_badge.xml
        ├── layout/
        │   └── activity_main.xml
        └── values/
            ├── themes.xml
            ├── colors.xml
            └── strings.xml
```

---

## Dépendances Jetpack

Dans `app/build.gradle` :

```groovy
def lifecycle_version = "2.8.7"
implementation "androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version"
implementation "androidx.lifecycle:lifecycle-livedata:$lifecycle_version"
implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"
```

---

## Concepts clés

### Pourquoi ViewModel ?

Sans ViewModel, toutes les variables d'instance de l'Activity sont **perdues à chaque rotation d'écran** (onDestroy → onCreate). `onSaveInstanceState` est une solution partielle : elle ne supporte que les types primitifs, pas les objets complexes ni les threads.

### Comment ViewModel survit à la rotation

```
Rotation d'écran
      │
      ▼
Activity.onDestroy()          ← Activity détruite
      │
      │    ViewModelStore ──── ViewModel ────── données intactes ✓
      │         (persiste)
      ▼
Activity.onCreate()           ← nouvelle instance
      │
ViewModelProvider(this)       ← retrouve le MÊME ViewModel
      │
.observe(this, ...)           ← re-branche l'observer
      │
UI à jour immédiatement ✓
```

### MutableLiveData vs LiveData

| | MutableLiveData | LiveData |
|---|---|---|
| Modifiable | ✓ (setValue / postValue) | ✗ (lecture seule) |
| Utilisé dans | ViewModel (interne) | Activity (exposé) |
| Objectif | Stocker et modifier | Observer sans modifier |

### setValue vs postValue

| Méthode | Thread | Usage |
|---|---|---|
| `setValue(val)` | Main thread uniquement | Actions bouton, logique UI |
| `postValue(val)` | N'importe quel thread | Réseau, BDD, background |

### SavedStateHandle (Bonus 2)

Persiste les données même après **kill processus** (quand Android tue l'app en arrière-plan). Les données sont sauvegardées dans le Bundle système et restaurées automatiquement.

```java
// Dans le ViewModel
Integer saved = savedStateHandle.get("count");
savedStateHandle.set("count", newVal);
```

---

## Fichiers à créer / modifier

| # | Fichier | Action |
|---|---------|--------|
| 1 | `build.gradle (Module: app)` | Remplacer + Sync Now |
| 2 | `res/values/colors.xml` | Remplacer |
| 3 | `res/values/themes.xml` (les 2) | Remplacer |
| 4 | `res/drawable/bg_badge.xml` | Créer (clic droit drawable → New) |
| 5 | `res/layout/activity_main.xml` | Remplacer |
| 6 | `CounterViewModel.java` | Créer (clic droit package → New → Java Class) |
| 7 | `MainActivity.java` | Remplacer |

---

## Tests du lab

### 1. Rotation d'écran
```
Incrémenter 10 fois → Ctrl+F11 (émulateur) → compteur conservé ✓
```

### 2. Thread background (postValue)
```
Appuyer sur "+ depuis Thread BG" → attendre 300ms → compteur +1 ✓
```

### 3. Process death (Bonus 2)
```bash
adb shell am kill com.example.viewmodellivedatademoenrichi
# Relancer l'app → compteur récupéré ✓
```

### 4. Thème sombre
```
Paramètres système → Dark mode → UI adaptée, valeur conservée ✓
```

---

## Tableau comparatif (résumé du cours)

| Critère | Sans ViewModel | Avec ViewModel + LiveData |
|---|---|---|
| Survie à la rotation | ✗ (perdu) | ✓ (ViewModelStore) |
| Mise à jour UI | Manuelle | Automatique (Observer) |
| Thread principal | Risque de crash | setValue / postValue sécurisé |
| Objets complexes | ✗ | ✓ (LiveData accepte tout) |
| Lifecycle-aware | ✗ | ✓ (pas de mise à jour si détruit) |
| Memory leak | Possible | Impossible (auto-unsubscribe) |
| Code MVVM propre | ✗ | ✓ |

---

## Architecture MVVM

```
┌─────────────────────────────────────────┐
│              MainActivity               │
│  (View — observe uniquement, pas de     │
│   logique métier)                       │
│                                         │
│  viewModel.getCount().observe(this, ─── │──→ onChanged(newCount) → tvCount.setText()
│  btnIncrement → viewModel.increment()   │
└────────────────────┬────────────────────┘
                     │ ViewModelProvider(this)
                     ▼
┌─────────────────────────────────────────┐
│           CounterViewModel              │
│  (ViewModel — logique métier)           │
│                                         │
│  MutableLiveData<Integer> countLiveData │
│  increment() → setValue(current + 1)   │
│  incrementFromBackground() → postValue  │
│  SavedStateHandle → persiste le count  │
└─────────────────────────────────────────┘
```

---

## Prérequis

- Android Studio Hedgehog ou supérieur
- Émulateur ou appareil API 24+
- JDK 8+

---

## Lancer le projet

1. **File → Open** → sélectionner ce dossier
2. Attendre la sync Gradle (automatique)
3. **Run ▶** sur un émulateur API 24+

---

*Lab réalisé dans le cadre du cours Android — Architecture MVVM avec Jetpack Lifecycle 2.8.7*
