# .github/scripts/ai_code_reviewer.py
import os
import sys
import subprocess
import openai # pip install openai (for OpenAI)
# import google.generativeai as genai # pip install google-generativeai (for Google Gemini)

def get_pr_diff(base_ref, head_ref):
    """获取 PR 的代码差异。"""
    try:
        # 获取所有 .java 文件的差异
        diff_command = f"git diff {base_ref} {head_ref} -- '*.java'"
        diff_output = subprocess.check_output(diff_command, shell=True, text=True, encoding='utf-8')
        return diff_output
    except subprocess.CalledProcessError as e:
        print(f"Error getting diff: {e}")
        sys.exit(1)

def get_file_content(file_path, ref):
    """获取特定文件的完整内容。"""
    try:
        # 使用 git show 获取特定 ref 下的文件内容
        content_command = f"git show {ref}:{file_path}"
        content_output = subprocess.check_output(content_command, shell=True, text=True, encoding='utf-8')
        return content_output
    except subprocess.CalledProcessError:
        # 如果文件不存在于该 ref，则返回空
        return ""

def generate_review_prompt(diff_content, changed_files_paths, base_ref, head_ref):
    """
    根据 diff 和文件路径生成发送给 LLM 的提示。
    为了提供更多上下文，我们尝试获取变更文件的完整内容。
    """
    prompt = f"""
    As a senior Java software engineer and code review expert, your responsibility is to thoroughly review the Java code changes in a GitHub Pull Request. You are expected to offer high-quality recommendations for code enhancements, and to identify potential bugs, performance issues, security risks, and areas where the code does not align with established Java best practices.

    Kindly give special consideration to the following aspects.：
    1.  **Code Quality and Readability**: Does it follow Java coding standards (e.g., naming conventions, code style, comments)? Is the code easy to understand and maintain?
    2.  **Performance**: Are there any signs of potential performance bottlenecks or inefficient code structures?
    3.  **Security**: Are there any common Java security vulnerabilities present (such as SQL injection, XXE, insecure deserialization, weak encryption, or path traversal)?
    4. **Concurrency and Thread Safety**: If multithreading is involved, are there any race conditions, deadlocks, or resource leaks? Are concurrency utilities used correctly?
    5.  **Exception Handling**: Are exceptions properly caught and handled? Are there any uncaught runtime exceptions?
    6.  **Resource Management**: Are resources (e.g., files, network connections, database connections) properly closed? Is try-with-resources being used where appropriate?
    7.  **Test Coverage**: Are there sufficient unit and integration tests for the newly added or modified logic? What additional test cases could be added?
    8.  **Design Patterns and Architecture**: Are design patterns used appropriately? Are there better design or abstraction alternatives?

    Kindly present your review findings in Markdown format. For each identified issue, please include the following:
    * **File path and line number** (if it can be pinpointed, for example `src/main/java/com/example/MyClass.java:42`)
    * **Issue description** (Clear and concise)
    * **Recommended improvements or code examples** (If there are)
    * **Seriousness** (Optional: For example `Critical`, `High`, `Medium`, `Low`, `Suggestion`)

    ---
    **Below are the Java code differences (diff) in the Pull Request.:**
    ```diff
    {diff_content}
    ```

    ---
    **Below is the full content of some changed files (to provide context, if the diff is insufficient)：**
    """
    for file_path in changed_files_paths.split(): # 假设 changed_files_paths 是空格分隔的
        if file_path.endswith('.java'):
            original_content = get_file_content(file_path, base_ref)
            modified_content = get_file_content(file_path, head_ref)

            if original_content or modified_content:
                prompt += f"\n### File: {file_path}\n"
                if original_content:
                    prompt += "\n**Original Content:**\n```java\n"
                    prompt += original_content[:2000] + ("\n...[truncated]" if len(original_content) > 2000 else "") # 截断以避免超出模型限制
                    prompt += "\n```\n"
                if modified_content and modified_content != original_content:
                    prompt += "\n**Modified Content:**\n```java\n"
                    prompt += modified_content[:2000] + ("\n...[truncated]" if len(modified_content) > 2000 else "") # 截断
                    prompt += "\n```\n"

    prompt += "\n---"
    prompt += "\nPlease begin your review and output the results directly."
    return prompt

def get_llm_response(prompt, api_key, model_name="gpt-4o"): # 或 "gemini-pro"
    """向 LLM 发送请求并获取响应。"""
    try:
        if model_name.startswith("gpt"):
            client = openai.OpenAI(api_key=api_key)
            response = client.chat.completions.create(
                model=model_name,
                messages=[
                    {"role": "system", "content": "You are a highly skilled Java code review expert."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.7,
                max_tokens=2000
            )
            return response.choices[0].message.content
        # elif model_name.startswith("gemini"):
        #     genai.configure(api_key=api_key)
        #     model = genai.GenerativeModel(model_name)
        #     response = model.generate_content(prompt)
        #     return response.text
        else:
            raise ValueError(f"Unsupported model: {model_name}")

    except Exception as e:
        print(f"Error getting LLM response: {e}")
        sys.exit(1)

if __name__ == "__main__":
    # 从环境变量获取参数
    base_ref = os.environ.get('GITHUB_BASE_REF')
    head_ref = os.environ.get('GITHUB_HEAD_REF')
    pull_request_number = os.environ.get('GITHUB_PR_NUMBER')
    repo_name = os.environ.get('GITHUB_REPOSITORY')
    llm_api_key = os.environ.get('LLM_API_KEY') # 统一使用 LLM_API_KEY 环境变量
    llm_model_name = os.environ.get('LLM_MODEL_NAME', 'gpt-4o') # 默认为 gpt-4o

    if not all([base_ref, head_ref, pull_request_number, repo_name, llm_api_key]):
        print("Missing required environment variables.")
        sys.exit(1)

    print(f"Reviewing PR #{pull_request_number} for {repo_name}...")
    print(f"Base Ref: {base_ref}, Head Ref: {head_ref}")

    # 切换到 base_ref 以便正确获取 diff
    subprocess.run(f"git fetch origin {base_ref}", shell=True, check=True)
    subprocess.run(f"git checkout {base_ref}", shell=True, check=True)

    # 获取所有变更的 .java 文件路径
    changed_java_files_command = f"git diff --name-only {base_ref} {head_ref} | grep '\\.java$'"
    changed_java_files = subprocess.check_output(changed_java_files_command, shell=True, text=True, encoding='utf-8').strip()
    changed_files_list = changed_java_files.split('\n') if changed_java_files else []

    if not changed_java_files:
        print("No Java files changed in this PR. Skipping AI review.")
        # 在 GitHub Actions 中，如果不想让这一步失败，可以 exit 0 或不输出任何内容
        sys.exit(0)

    # 再次切换到 head_ref 以获取最新的文件内容
    subprocess.run(f"git checkout {head_ref}", shell=True, check=True)

    diff_content = get_pr_diff(base_ref, head_ref)
    prompt = generate_review_prompt(diff_content, changed_java_files, base_ref, head_ref)

    print("Sending request to LLM...")
    review_output = get_llm_response(prompt, llm_api_key, llm_model_name)

    print("\n--- AI Review Output ---")
    print(review_output)

    # 将审查结果发布到 PR 评论 (使用 GitHub CLI)
    # gh cli 已经预装在 GitHub Actions 运行器中
    try:
        comment_body = f"## 🤖 AI Code Review Suggestions\n\n{review_output}\n\n---\n*Generated by AI. Please use your judgment.*"
        # 使用 gh pr comment 命令发布评论
        subprocess.run(
            ["gh", "pr", "comment", pull_request_number, "--repo", repo_name, "-F", "-"],
            input=comment_body,
            text=True,
            check=True
            )
        print(f"\nSuccessfully posted AI review comment to PR #{pull_request_number}.")
    except subprocess.CalledProcessError as e:
        print(f"Error posting comment to PR: {e}")
        sys.exit(1)