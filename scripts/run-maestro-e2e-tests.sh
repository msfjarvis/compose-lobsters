#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MAESTRO_TESTS_DIR="${SCRIPT_DIR}/../maestro/tests"

echo "Running Maestro E2E Test Suite for Claw"
echo "===================================================="
echo ""

if ! command -v maestro &> /dev/null; then
    echo "❌ Error: maestro is not installed or not in PATH"
    echo "   Install from: https://maestro.mobile.dev/getting-started/installing-maestro"
    exit 1
fi

echo "✅ Maestro found: $(maestro --version)"
echo ""

FAILED_TESTS=()
PASSED_TESTS=()
TOTAL_TESTS=0

run_test_suite() {
    local suite_name=$1
    local suite_dir=$2
    
    echo "📂 Running ${suite_name} tests..."
    
    if [[ ! -d "${suite_dir}" ]]; then
        echo "⚠️  Directory not found: ${suite_dir}"
        return
    fi
    
    for test_file in "${suite_dir}"/*.yaml; do
        if [[ ! -f "${test_file}" ]]; then
            continue
        fi
        
        local test_name
        test_name=$(basename "${test_file}")
        TOTAL_TESTS=$((TOTAL_TESTS + 1))
        
        echo -n "  ▶ ${test_name}... "
        
        if maestro test "${test_file}" > /dev/null 2>&1; then
            echo "✅ PASSED"
            PASSED_TESTS+=("${suite_name}/${test_name}")
        else
            echo "❌ FAILED"
            FAILED_TESTS+=("${suite_name}/${test_name}")
        fi
        
        # Add delay between tests to prevent timing issues
        sleep 3
    done
    
    echo ""
}

run_test_suite "Smoke" "${MAESTRO_TESTS_DIR}/smoke"
run_test_suite "Features" "${MAESTRO_TESTS_DIR}/features"
run_test_suite "Regression" "${MAESTRO_TESTS_DIR}/regression"

echo "===================================================="
echo "Test Results Summary"
echo "===================================================="
echo "Total Tests: ${TOTAL_TESTS}"
echo "Passed: ${#PASSED_TESTS[@]}"
echo "Failed: ${#FAILED_TESTS[@]}"
echo ""

if [[ ${#FAILED_TESTS[@]} -gt 0 ]]; then
    echo "❌ Failed Tests:"
    for test in "${FAILED_TESTS[@]}"; do
        echo "   - ${test}"
    done
    echo ""
    exit 1
else
    echo "✅ All tests passed!"
    exit 0
fi
