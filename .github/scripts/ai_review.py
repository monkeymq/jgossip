import os
import openai
from github import Github


# def load_event():
#     event_path = os.environ["GITHUB_EVENT_PATH"]
#     with open(event_path, "r") as f:
#         return json.load(f)
#
# def get_pr_diff(diff_url, github_token):
#     response = requests.get(diff_url, headers={
#         "Authorization": f"token {github_token}",
#         "Accept": "application/vnd.github.v3.diff"
#     })
#     response.raise_for_status()
#     return response.text


# def load_diff(file_path="pr.diff"):
#     """Load the diff content from file."""
#     if not os.path.exists(file_path):
#         print("❌ Diff file not found.")
#         return ""
#     with open(file_path, "r", encoding="utf-8") as f:
#         return f.read()

OPENAI_API_KEY = os.environ['OPENAI_API_KEY']
OPENAI_API_ENDPOINT = os.environ.get('OPENAI_API_ENDPOINT')
GITHUB_TOKEN = os.environ['GITHUB_TOKEN']
PR_NUMBER = int(os.environ['PR_NUMBER'])
REPO_NAME = os.environ['REPO']

def call_gpt():
    """Call AI to review the diff."""
    api_key = OPENAI_API_KEY
    if not api_key:
        raise ValueError("❌ OPENAI_API_KEY is not set.")

    api_endpoint = OPENAI_API_ENDPOINT

    client = openai.OpenAI(
        api_key=api_key,
        base_url=api_endpoint,
        default_headers={"api-key": api_key},
    )

    gh = Github(GITHUB_TOKEN)
    repo = gh.get_repo(REPO_NAME)
    pr = repo.get_pull(PR_NUMBER)

    diff = pr.patch_url
    files = pr.get_files()
    changed_files = []
    for f in files:
        if f.status != "removed":
            file_content = f"File: {f.filename}\nPatch:\n{f.patch}\n"
            changed_files.append(file_content)

    diff_text = "\n\n".join(changed_files)


    prompt = f"""
You are a senior Java code reviewer.

Please analyze the following GitHub Pull Request diff. Focus specifically on:
1. **Performance issues** (e.g., inefficient loops, unnecessary resource usage)
2. **Security issues** (e.g., SQL injection, thread safety, user input validation)
3. **Code style issues** (e.g., naming, code duplication, missing comments)

Respond in **Markdown** format, organized as:

### 🐢 Performance Issues:
(if none, say "No significant performance issues found.")

### 🔒 Security Issues:
...

### 🎨 Code Style Issues:
...

### ✅ Overall Suggestions:
...

Here is the diff:

```
{diff_text}
```
"""

    print("🚀 Sending diff to AI for review...")
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {"role": "system", "content": "You are a Java code review expert."},
            {"role": "user", "content": prompt},
        ],
        temperature=0.2,
    )
    review_comment = response.choices[0].message.content
    pr.create_issue_comment(f"🤖 **AI Code Review:**\n\n{review_comment}")
    print("✅ Review comment posted.")



# def post_pr_comment(review_text):
#     """Post review result as a comment on the PR."""
#     repo = os.getenv("GITHUB_REPOSITORY")
#     pr_number = os.getenv("PR_NUMBER")
#     token = os.getenv("GITHUB_TOKEN")
#
#     url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
#     headers = {
#         "Authorization": f"token {token}",
#         "Accept": "application/vnd.github.v3+json",
#     }
#     payload = {"body": review_text}
#
#     print("📝 Posting review comment to PR...")
#     response = requests.post(url, headers=headers, json=payload)
#     if response.status_code != 201:
#         print(f"❌ Failed to post comment: {response.status_code}")
#         print(response.text)
#     else:
#         print("✅ Review comment posted successfully.")


def main():
#     diff = load_diff()
#     if not diff.strip():
#         print("ℹ️ No diff content found. Skipping review.")
#         return
#     review = call_gpt(diff)
#     post_pr_comment(review)

    event = load_event()
    github_token = os.environ["GITHUB_TOKEN"]

    diff_url = event["pull_request"]["diff_url"]
    comments_url = event["pull_request"]["comments_url"]

    diff = get_pr_diff(diff_url, github_token)
    review = call_gpt(diff)
    post_comment(comments_url, f"🤖 **AI review feedback：**\n\n{review}", github_token)


if __name__ == "__main__":
    main()
