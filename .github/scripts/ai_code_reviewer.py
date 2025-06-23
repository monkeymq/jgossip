import openai
import os
import requests

def load_diff(file_path='pr.diff'):
    """Load the diff content from file."""
    if not os.path.exists(file_path):
        print("❌ Diff file not found.")
        return ""
    with open(file_path, 'r', encoding='utf-8') as f:
        return f.read()

def call_gpt(diff_text):
    """Call OpenAI API to review the diff content."""
    openai.api_key = os.getenv("OPENAI_API_KEY")
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
    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[
            {"role": "system", "content": "You are a Java code review expert."},
            {"role": "user", "content": prompt}
        ],
        temperature=0.2
    )
    return response['choices'][0]['message']['content']

def post_pr_comment(review_text):
    """Post review result as a comment on the PR."""
    repo = os.getenv("GITHUB_REPOSITORY")
    pr_number = os.getenv("PR_NUMBER")
    token = os.getenv("GITHUB_TOKEN")

    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {
        "Authorization": f"token {token}",
        "Accept": "application/vnd.github.v3+json"
    }
    payload = {"body": review_text}

    print("📝 Posting review comment to PR...")
    response = requests.post(url, headers=headers, json=payload)
    if response.status_code != 201:
        print(f"❌ Failed to post comment: {response.status_code}")
        print(response.text)
    else:
        print("✅ Review comment posted successfully.")

def main():
    diff = load_diff()
    if not diff.strip():
        print("ℹ️ No diff content found. Skipping review.")
        return
    review = call_gpt(diff)
    post_pr_comment(review)

if __name__ == "__main__":
    main()
