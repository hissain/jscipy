#!/bin/bash
set -e

# ============================================================================
# jSciPy Full Project Verification Script
# Platform-agnostic: Works on Windows (Git Bash/MSYS/Cygwin), Linux, and macOS
# ============================================================================

# Colors for output (ANSI escape codes)
RED=$'\033[0;31m'
GREEN=$'\033[0;32m'
BLUE=$'\033[0;34m'
YELLOW=$'\033[1;33m'
NC=$'\033[0m' # No Color

echo "${BLUE}🚀 Starting Full Project Verification...${NC}"

# ============================================================================
# Python Virtual Environment Setup
# ============================================================================

# Detect Python executable in virtual environment
get_venv_python() {
    if [ -f ".venv/bin/python" ]; then
        echo ".venv/bin/python"
    elif [ -f ".venv/bin/python3" ]; then
        echo ".venv/bin/python3"
    elif [ -f ".venv/Scripts/python.exe" ]; then
        echo ".venv/Scripts/python.exe"
    elif [ -f ".venv/Scripts/python" ]; then
        echo ".venv/Scripts/python"
    else
        echo ""
    fi
}

# Detect system Python
get_system_python() {
    if command -v python3 &> /dev/null; then
        echo "python3"
    elif command -v python &> /dev/null; then
        echo "python"
    else
        echo ""
    fi
}

if [ -d ".venv" ]; then
    echo "${BLUE}🐍 Found .venv virtual environment...${NC}"
    PYTHON_CMD=$(get_venv_python)
    if [ -n "$PYTHON_CMD" ]; then
        echo "${GREEN}✓ Using Python from: $PYTHON_CMD${NC}"
    else
        echo "${YELLOW}⚠ Virtual environment found but couldn't locate Python executable${NC}"
        PYTHON_CMD=$(get_system_python)
        if [ -z "$PYTHON_CMD" ]; then
            echo "${RED}❌ No Python found!${NC}"
            exit 1
        fi
    fi
else
    echo "${YELLOW}⚠ No .venv found. Creating virtual environment...${NC}"
    
    PYTHON_CMD_CREATE=$(get_system_python)
    if [ -z "$PYTHON_CMD_CREATE" ]; then
        echo "${RED}❌ Python is not installed!${NC}"
        echo "Please install Python 3.7+ and try again."
        exit 1
    fi
    
    echo "${BLUE}Creating .venv with $PYTHON_CMD_CREATE...${NC}"
    $PYTHON_CMD_CREATE -m venv .venv
    
    PYTHON_CMD=$(get_venv_python)
    if [ -z "$PYTHON_CMD" ]; then
        echo "${RED}❌ Failed to create virtual environment${NC}"
        exit 1
    fi
    
    echo "${GREEN}✓ Virtual environment created${NC}"
    echo "${BLUE}📦 Installing Python dependencies...${NC}"
    $PYTHON_CMD -m pip install --upgrade pip > /dev/null 2>&1 || true
    $PYTHON_CMD -m pip install -r python/requirements.txt
    echo "${GREEN}✓ Dependencies installed${NC}"
fi

# ============================================================================
# Java/JDK Detection
# ============================================================================

# Detect OS type
detect_os() {
    case "$OSTYPE" in
        linux*)   echo "linux" ;;
        darwin*)  echo "macos" ;;
        msys*)    echo "windows" ;;
        cygwin*)  echo "windows" ;;
        mingw*)   echo "windows" ;;
        *)        
            # Fallback detection
            if [ -d "/Applications" ] && [ -d "/System" ]; then
                echo "macos"
            elif [ -f "/etc/os-release" ]; then
                echo "linux"
            elif [ -n "$WINDIR" ]; then
                echo "windows"
            else
                echo "unknown"
            fi
            ;;
    esac
}

OS_TYPE=$(detect_os)
echo "${BLUE}ℹ️  Detected OS: $OS_TYPE${NC}"

# Auto-detect JAVA_HOME on Windows if not set
if [[ -z "$JAVA_HOME" && "$OS_TYPE" == "windows" ]]; then
    echo "${BLUE}🔍 JAVA_HOME not set. Attempting to locate on Windows...${NC}"
    # Common paths for JDKs on Windows
    for JDK_PATH in \
        "/c/Program Files/Microsoft/jdk-"* \
        "/c/Program Files/Eclipse Adoptium/jdk-"* \
        "/c/Program Files/Java/jdk"*; do
        if [ -d "$JDK_PATH" ]; then
            echo "${GREEN}Found JDK at: $JDK_PATH${NC}"
            export JAVA_HOME="$JDK_PATH"
            break
        fi
    done
fi

if [[ -z "$JAVA_HOME" ]]; then
    echo "${BLUE}ℹ️  JAVA_HOME is: (Not Set)${NC}"
else
    echo "${BLUE}ℹ️  JAVA_HOME is: $JAVA_HOME${NC}"
fi

# Check if Java is available
echo "${BLUE}🔍 Checking Java installation...${NC}"
if ! command -v java &> /dev/null; then
    echo "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo "${RED}❌ Java is NOT installed on this system!${NC}"
    echo "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    echo "${YELLOW}jSciPy requires Java Development Kit (JDK) 8 or higher for building.${NC}"
    echo ""
    echo "${BLUE}📥 Installation Instructions:${NC}"
    echo ""
    
    case "$OS_TYPE" in
        windows)
            echo "${GREEN}Windows:${NC}"
            echo "  • Download Temurin JDK 8: https://adoptium.net/temurin/releases/?version=8"
            echo "  • Or use Chocolatey: choco install temurin8jdk"
            echo "  • Or use winget: winget install EclipseAdoptium.Temurin.8.JDK"
            ;;
        macos)
            echo "${GREEN}macOS:${NC}"
            echo "  brew install openjdk@8"
            echo "  Or download from: https://adoptium.net/"
            ;;
        linux)
            echo "${GREEN}Linux:${NC}"
            echo "  Ubuntu/Debian: sudo apt update && sudo apt install openjdk-8-jdk"
            echo "  Fedora/RHEL:   sudo dnf install java-1.8.0-openjdk-devel"
            echo "  Arch:          sudo pacman -S jdk8-openjdk"
            ;;
        *)
            echo "Please install JDK 8 from: https://adoptium.net/"
            ;;
    esac
    
    echo ""
    echo "${GREEN}💡 Alternative: Let Gradle auto-download JDK${NC}"
    echo "   Simply run ./gradlew build - Gradle will automatically download JDK 8!"
    echo ""
    echo "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    exit 1
else
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
    echo "${GREEN}✓ Java Runtime found: $JAVA_VERSION${NC}"
    
    # Check if it's JDK (has javac) or just JRE
    if command -v javac &> /dev/null; then
        JAVAC_VERSION=$(javac -version 2>&1 | awk '{print $2}')
        echo "${GREEN}✓ JDK detected: javac $JAVAC_VERSION${NC}"
    else
        echo "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo "${YELLOW}⚠️  WARNING: Java Runtime (JRE) found, but NOT JDK!${NC}"
        echo "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo ""
        echo "${YELLOW}Building jSciPy requires the Java Development Kit (JDK).${NC}"
        echo "${YELLOW}You currently have only the Java Runtime Environment (JRE).${NC}"
        echo ""
        echo "${BLUE}📥 Install JDK:${NC}"
        echo ""
        
        case "$OS_TYPE" in
            windows)
                echo "${GREEN}Windows:${NC}"
                echo "  • Download Temurin JDK 8: https://adoptium.net/temurin/releases/?version=8"
                echo "  • Or use Chocolatey: choco install temurin8jdk"
                echo "  • Or use winget: winget install EclipseAdoptium.Temurin.8.JDK"
                ;;
            macos)
                echo "${GREEN}macOS:${NC}"
                echo "  brew install openjdk@8"
                ;;
            linux)
                echo "${GREEN}Linux:${NC}"
                echo "  Ubuntu/Debian: sudo apt install openjdk-8-jdk"
                echo "  Fedora/RHEL:   sudo dnf install java-1.8.0-openjdk-devel"
                ;;
            *)
                echo "Please install JDK 8 from: https://adoptium.net/"
                ;;
        esac
        
        echo ""
        echo "${GREEN}💡 Alternative: Let Gradle auto-download JDK${NC}"
        echo "   Use the Gradle Wrapper: ./gradlew build"
        echo "   Gradle will automatically download JDK 8 to ~/.gradle/jdks/"
        echo "   (No system installation required! Works for this project only)"
        echo ""
        echo "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo "${YELLOW}Continuing anyway - Gradle may auto-provision JDK...${NC}"
        echo "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
        echo ""
    fi
fi

# ============================================================================
# Run Verification Steps
# ============================================================================

echo "${BLUE}1️⃣  Cleaning datasets and python figures...${NC}"
rm -rf datasets/*
rm -rf python/figs/*

echo "${BLUE}2️⃣  Generating Ground Truth Data (Python)...${NC}"
$PYTHON_CMD python/generate_all_data.py

echo "${BLUE}3️⃣  Running Java Tests...${NC}"
./gradlew clean test

echo "${BLUE}4️⃣  Generating Comparison Plots...${NC}"
$PYTHON_CMD python/generate_all_plots.py
echo "Generating Accuracy Table..."
$PYTHON_CMD python/generate_accuracy_plot.py

echo "Generating Comparison Table..."
$PYTHON_CMD python/generate_comparison_table.py

echo "${GREEN}✅ All verification steps completed successfully!${NC}"
