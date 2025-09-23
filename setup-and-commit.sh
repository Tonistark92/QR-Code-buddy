#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_color() {
    color=$1
    message=$2
    echo -e "${color}${message}${NC}"
}

# Function to check if hooks are installed
check_hooks_installed() {
    if [[ -f ".git/hooks/pre-commit" && -f ".git/hooks/pre-push" ]]; then
        return 0  # Hooks are installed
    else
        return 1  # Hooks are not installed
    fi
}

# Function to install hooks
install_hooks() {
    print_color $BLUE "Installing Git hooks..."

    # Check if hook files exist in hooks directory
    if [[ ! -f "hooks/pre-commit" ]]; then
        print_color $RED "❌ Error: hooks/pre-commit file not found!"
        exit 1
    fi

    if [[ ! -f "hooks/pre-push" ]]; then
        print_color $RED "❌ Error: hooks/pre-push file not found!"
        exit 1
    fi

    # Create .git/hooks directory if it doesn't exist
    mkdir -p .git/hooks

    # Copy hooks to .git/hooks/
    cp hooks/pre-commit .git/hooks/pre-commit
    cp hooks/pre-push .git/hooks/pre-push

    # Make hooks executable
    chmod +x .git/hooks/pre-commit
    chmod +x .git/hooks/pre-push

    print_color $GREEN "✅ Git hooks installed successfully!"
}

# Function to run Spotless formatting
run_spotless() {
    print_color $BLUE "Running Spotless formatting..."

    # Check if gradlew exists
    if [[ ! -f "./gradlew" ]]; then
        print_color $RED "❌ Error: gradlew not found in current directory!"
        exit 1
    fi

    # Make gradlew executable if it's not
    chmod +x ./gradlew

    # Run Spotless apply
    if ./gradlew spotlessApply; then
        print_color $GREEN "✅ Spotless formatting completed successfully!"
        return 0
    else
        print_color $RED "❌ Spotless formatting failed!"
        return 1
    fi
}

# Function to check if there are changes to commit
has_changes() {
    if [[ -n $(git status --porcelain) ]]; then
        return 0  # Has changes
    else
        return 1  # No changes
    fi
}

# Function to get commit message from user
get_commit_message() {
    print_color $YELLOW "📝 Enter your commit message:"
    read -r commit_message

    # Check if message is empty
    if [[ -z "$commit_message" ]]; then
        print_color $RED "❌ Commit message cannot be empty!"
        get_commit_message  # Recursively ask again
    fi

    echo "$commit_message"
}

# Function to pause before exit
pause_before_exit() {
    print_color $BLUE "Press [Enter] to exit..."
    read -r
}

# Main script execution
main() {
    print_color $BLUE "🚀 Starting Git Hooks Setup and Auto-commit Script"
    print_color $BLUE "=================================================="

    # Step 1: Check and install hooks if needed
    if check_hooks_installed; then
        print_color $GREEN "✅ Git hooks are already installed."
    else
        print_color $YELLOW "⚠️  Git hooks are not installed. Installing now..."
        install_hooks
    fi

    # Step 2: Run Spotless formatting
    if ! run_spotless; then
        print_color $RED "❌ Script failed due to Spotless formatting errors."
        pause_before_exit
        exit 1
    fi

    # Step 3: Check if there are changes after formatting
    if has_changes; then
        print_color $YELLOW "📋 Changes detected after Spotless formatting:"
        git status --short
        print_color $BLUE ""

        # Step 4: Add all changes
        print_color $BLUE "Adding all changes to git..."
        git add .

        # Step 5: Get commit message from user
        commit_message=$(get_commit_message)

        # Step 6: Commit changes
        print_color $BLUE "Committing changes..."
        if git commit -m "$commit_message"; then
            print_color $GREEN "✅ Changes committed successfully!"

            # Step 7: Push changes
            print_color $BLUE "Pushing changes to remote repository..."
            current_branch=$(git branch --show-current)
            print_color $YELLOW "Pushing to branch: $current_branch"

            if git push origin "$current_branch"; then
                print_color $GREEN "✅ Changes pushed successfully!"
            else
                print_color $RED "❌ Failed to push changes. Please check your remote configuration."
            fi
        else
            print_color $RED "❌ Failed to commit changes."
        fi
    else
        print_color $GREEN "✅ No changes detected after Spotless formatting. Repository is clean!"
    fi

    print_color $BLUE ""
    print_color $GREEN "🎉 Script completed successfully!"
    pause_before_exit
}

# Run the main function
main "$@"