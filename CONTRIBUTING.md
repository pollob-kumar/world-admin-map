# How to Contribute

This guide is for all contributors, so we can work together, stay organized, and collaborate smoothly on GitHub.

---


## Repository Access

* All collaborators can create their own branches.
* Direct push to protected branches is restricted.
---


## Branch Structure (Very Important)

### 1. **main branch**

    * ❌ Direct push is not allowed
    * ❌ Direct PR is not allowed (except from develop)
    * ✅ For now, **Pollob Kumar** can merge PR from `develop`. In the future, others will be added for this task.

### 2. **develop branch**

    * ❌ Direct push is not allowed
    * ✅ All team members can create Pull Request here
    * 🔄 This is the main working branch

### 3. **Developer Branches**

    * Each developer must create their own branch
    * Example:

        * `pollob-feature`
        * `pollob/project-setup`
---

## Contribute Process(Step)

### Step 3.1: Clone the Repository (First Time Only)

```bash
git clone https://github.com/pollob-kumar/world-admin-map.git
cd world-admin-map
```

### Step 2: Create Your Own Branch

```bash
git checkout develop
git pull origin develop

git checkout -b your-branch-name
```

Example:

```bash
git checkout -b pollob-feature
```

### Step 3: Do Your Work and Push

```bash
git add .
git commit -m "your message here"
git push origin your-branch-name
```

### Step 4: Create Pull Request to Develop

1. Go to GitHub
2. Click **“Compare & pull request”**
3. Select:
    * Base branch: `develop`
    * Compare branch: `your-branch-name`

4. Add meaningful title & description
5. Submit PR(Pull Request)


### Step 5: Merge Flow

```
Developer Branch → develop → main
```

* Team reviews PR → merge into `develop`
* After testing → **Pollob Kumar** will merge `develop → main`
---


## ⚠️ Important Rules

- Never push directly to `main`  
- Never push directly to `develop`

Always follow this flow:
```
your branch → develop → main
```



## Always Pull Latest Code Before Work

```bash
git checkout develop
git pull origin develop

git checkout your-branch-name
git merge develop
```

## Good Practices

* Use clear commit messages
* Work only on your own branch
* Create small & meaningful PRs
* Review others’ code when needed

<br>

**Let’s build World Admin Map together!**
Teamwork makes the project clean, scalable, and professional 💪
