#!/bin/bash

# Safer color setup (works in most shells)
RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
YELLOW=$(tput setaf 3)
BLUE=$(tput setaf 4)
NC=$(tput sgr0) # Reset

# Function to print colored output
print_color() {
    color=$1
    message=$2
    echo "${color}${message}${NC}"
}

# Function to check if hooks are installed
check_hooks_installed() {
    if [[ -f ".git/hooks/pre-commit" && -f ".git/hooks/pre-push" ]]; then
        return 0
    else
        return 1
    fi
}

# Function to install hooks
install_hooks() {
    print_color "$BLUE" "Installing Git hooks..."

    if [[ ! -f "hooks/pre-commit" ]]; then
        print_color "$RED" "❌ Error: hooks/pre-commit file not found!"
        exit 1
    fi

    if [[ ! -f "hooks/pre-push" ]]; then
        print_color "$RED" "❌ Error: hooks/pre-push file not found!"
        exit 1
    fi

    mkdir -p .git/hooks
    cp hooks/pre-commit .git/hooks/pre-commit
    cp hooks/pre-push .git/hooks/pre-push
    chmod +x .git/hooks/pre-commit .git/hooks/pre-push

    print_color "$GREEN" "✅ Git hooks installed successfully!"
}

# Function to run Spotless formatting
run_spotless() {
    print_color "$BLUE" "Running Spotless formatting..."

    if [[ ! -f "./gradlew" ]]; then
        print_color "$RED" "❌ Error: gradlew not found!"
        exit 1
    fi

    chmod +x ./gradlew

    if ./gradlew spotlessApply; then
        print_color "$GREEN" "✅ Spotless formatting completed successfully!"
    else
        print_color "$RED" "❌ Spotless formatting failed!"
        return 1
    fi
}

# Function to check if there are changes
has_changes() {
    [[ -n $(git status --porcelain) ]]
}

# Function to get commit message from user
get_commit_message() {
    echo "" # blank line for clarity
    echo "📝 Enter your commit message:"
    read -r commit_message

    if [[ -z "$commit_message" ]]; then
        print_color "$RED" "❌ Commit message cannot be empty!"
        get_commit_message
    fi

    echo "$commit_message"
}

# Function to pause
pause_before_exit() {
    echo ""
    read -rp "Press [Enter] to exit..."
}

main() {
    print_color "$BLUE" "🚀 Starting Git Hooks Setup and Auto-commit Script"
    echo "=================================================="

    if check_hooks_installed; then
        print_color "$GREEN" "✅ Git hooks are already installed."
    else
        print_color "$YELLOW" "⚠️  Git hooks are not installed. Installing now..."
        install_hooks
    fi

    if ! run_spotless; then
        pause_before_exit
        exit 1
    fi

    if has_changes; then
        print_color "$YELLOW" "📋 Changes detected after Spotless formatting:"
        git status --short
        echo ""

        print_color "$BLUE" "Adding all changes..."
        git add .

        commit_message=$(get_commit_message)

        print_color "$BLUE" "Committing changes..."
        if git commit -m "$commit_message"; then
            print_color "$GREEN" "✅ Changes committed successfully!"
            current_branch=$(git branch --show-current)
            print_color "$YELLOW" "Pushing to branch: $current_branch"
            if git push origin "$current_branch"; then
                print_color "$GREEN" "✅ Changes pushed successfully!"
            else
                print_color "$RED" "❌ Failed to push changes."
            fi
        else
            print_color "$RED" "❌ Failed to commit changes."
        fi
    else
        print_color "$GREEN" "✅ No changes detected after Spotless formatting."
    fi

    echo ""
    print_color "$GREEN" "🎉 Script completed successfully!"
    pause_before_exit
}

main "$@"
