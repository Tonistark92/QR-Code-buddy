#!/bin/bash

# ================================
# Git Auto-format + Commit Script
# ================================

# Colors (only if terminal supports it)
if [ -t 1 ]; then
  RED=$(tput setaf 1)
  GREEN=$(tput setaf 2)
  YELLOW=$(tput setaf 3)
  BLUE=$(tput setaf 4)
  NC=$(tput sgr0) # reset color
else
  RED=""; GREEN=""; YELLOW=""; BLUE=""; NC=""
fi

print_color() { echo -e "${1}${2}${NC}"; }

print_color "$BLUE" "🚀 Starting Git Auto-format + Commit Script"
echo "=================================================="

# Step 1: Run Spotless
print_color "$BLUE" "Running Spotless formatting..."
./gradlew spotlessApply

if [ $? -ne 0 ]; then
  print_color "$RED" "❌ Spotless failed!"
  exit 1
fi

print_color "$GREEN" "✅ Spotless formatting applied successfully!"

# Step 2: Stage changes
git add .

# Step 3: Commit message handling
if [ -t 0 ]; then
  # Interactive terminal → ask user
  print_color "$YELLOW" "📝 Enter your commit message (press Ctrl+D when done):"

  # Read multiple lines into a variable
  commit_message=$(</dev/stdin)

  if [ -z "$commit_message" ]; then
    print_color "$RED" "❌ Commit message cannot be empty!"
    exit 1
  fi
else
  # Non-interactive (hooks/CI) → default message
  commit_message="Is coding pushed"
fi
# Step 4: Commit
if git commit -m "$commit_message"; then
  print_color "$GREEN" "✅ Commit created successfully!"
else
  print_color "$YELLOW" "ℹ️ No changes to commit."
  exit 0
fi

# Step 5: Push to current branch
current_branch=$(git branch --show-current)
print_color "$BLUE" "📤 Pushing changes to branch: $current_branch..."

if git push origin "$current_branch"; then
  print_color "$GREEN" "✅ Push successful!"
else
  print_color "$RED" "❌ Push failed. Please check your remote setup."
fi

print_color "$GREEN" "🎉 Done!"
