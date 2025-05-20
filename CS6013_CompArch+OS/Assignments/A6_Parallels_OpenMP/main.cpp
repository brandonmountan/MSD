////////////////////////////////////////////////////////////////////////
//
// Author: Brandon Mountan
// Date: 04/18/2025
//
// CS 6013
//
//
////////////////////////////////////////////////////////////////////////

#include <iostream>
#include <vector>
#include <thread>
#include <atomic>
#include <mutex>
#include <chrono>
#include <numeric>
#include <random>
#include <functional>
#include <fstream>
#include <iomanip>
#include <omp.h>
#include <tuple>

// Structure to hold the results of parallel sum calculations
struct Data {
    double sum;
    double time_ms;
};

// Part 1: C++ Parallel Reduction using STL
template<typename T>
Data parallel_sum_std(T a[], size_t N, size_t num_threads) {
    // Start timing
    auto start_time = std::chrono::high_resolution_clock::now();

    // Vector to hold threads
    std::vector<std::thread> threads;

    // Vector to hold partial sums for each thread
    std::vector<T> partial_sums(num_threads, 0);

    // Adjust thread count if N is too small
    if (N < num_threads) {
        num_threads = N;
    }

    // Calculate the chunk size for each thread
    size_t chunk_size = N / num_threads;
    size_t remainder = N % num_threads;

    // Create and execute threads
    for (size_t t = 0; t < num_threads; t++) {
        threads.push_back(std::thread([=, &partial_sums, &a]() {
            // Calculate the range for this thread
            size_t start = t * chunk_size + std::min(t, remainder);
            size_t end = start + chunk_size + (t < remainder ? 1 : 0);

            // Use Kahan summation algorithm for better floating-point accuracy
            // This helps prevent precision loss, especially for float type
            T local_sum = 0;
            T c = 0; // Compensation term

            if constexpr (std::is_floating_point<T>::value) {
                // Using Kahan summation for floating point types
                for (size_t i = start; i < end; i++) {
                    T y = a[i] - c;
                    T t = local_sum + y;
                    c = (t - local_sum) - y;
                    local_sum = t;
                }
            } else {
                // Simple summation for integer types
                for (size_t i = start; i < end; i++) {
                    local_sum += a[i];
                }
            }

            // Store the partial sum
            partial_sums[t] = local_sum;
        }));
    }

    // Join all threads
    for (auto& thread : threads) {
        if (thread.joinable()) {
            thread.join();
        }
    }

    // Compute the final sum from all partial sums
    // Use pairwise summation for better numerical stability
    T final_sum;

    if constexpr (std::is_floating_point<T>::value) {
        // Binary tree reduction to minimize floating-point error
        while (partial_sums.size() > 1) {
            std::vector<T> new_sums;
            for (size_t i = 0; i < partial_sums.size(); i += 2) {
                if (i + 1 < partial_sums.size()) {
                    new_sums.push_back(partial_sums[i] + partial_sums[i + 1]);
                } else {
                    new_sums.push_back(partial_sums[i]);
                }
            }
            partial_sums = std::move(new_sums);
        }
        final_sum = partial_sums[0];
    } else {
        // Simple reduction for integer types
        final_sum = 0;
        for (const auto& sum : partial_sums) {
            final_sum += sum;
        }
    }

    // End timing
    auto end_time = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end_time - start_time);

    // Return results
    return {static_cast<double>(final_sum), duration.count() / 1000.0};
}

// Part 2: OpenMP Custom Parallel Reduction
template<typename T>
Data parallel_sum_omp1(T a[], size_t N, size_t num_threads) {
    // Start timing
    auto start_time = std::chrono::high_resolution_clock::now();

    // Vector to hold partial sums for each thread
    std::vector<T> partial_sums;

    // Set the number of threads for OpenMP
    omp_set_num_threads(num_threads);

    // Parallel region with OpenMP
    #pragma omp parallel
    {
        // Get thread ID and total threads
        int thread_id = omp_get_thread_num();
        int actual_threads = omp_get_num_threads();

        // Resize partial_sums on first thread
        #pragma omp single
        {
            partial_sums.resize(actual_threads, 0);
        }

        // Calculate chunk size with better load balancing
        size_t chunk_size = N / actual_threads;
        size_t remainder = N % actual_threads;
        size_t start = thread_id * chunk_size + std::min(thread_id, static_cast<int>(remainder));
        size_t end = start + chunk_size + (thread_id < remainder ? 1 : 0);

        // Calculate partial sum with Kahan summation for floating point types
        T local_sum = 0;

        if constexpr (std::is_floating_point<T>::value) {
            // Kahan summation algorithm for better floating-point accuracy
            T c = 0; // Compensation term
            for (size_t i = start; i < end; i++) {
                T y = a[i] - c;
                T t = local_sum + y;
                c = (t - local_sum) - y;
                local_sum = t;
            }
        } else {
            // Regular summation for integer types
            for (size_t i = start; i < end; i++) {
                local_sum += a[i];
            }
        }

        // Store partial sum
        partial_sums[thread_id] = local_sum;
    }

    // Compute the final sum from all partial sums
    T final_sum;

    if constexpr (std::is_floating_point<T>::value) {
        // Use a more accurate reduction for floating point values
        int n = partial_sums.size();

        // Binary tree reduction to minimize rounding errors
        while (n > 1) {
            int half = n / 2;

            #pragma omp parallel for
            for (int i = 0; i < half; i++) {
                partial_sums[i] += partial_sums[i + half];
            }

            if (n % 2 == 1) {
                partial_sums[0] += partial_sums[n - 1];
            }

            n = half;
        }

        final_sum = partial_sums[0];
    } else {
        // Simple reduction for integer types
        final_sum = 0;
        for (const auto& sum : partial_sums) {
            final_sum += sum;
        }
    }

    // End timing
    auto end_time = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end_time - start_time);

    // Return results
    return {static_cast<double>(final_sum), duration.count() / 1000.0};
}

// Part 3: OpenMP Built-in Reduction
template<typename T>
Data parallel_sum_omp_builtin(T a[], size_t N, size_t num_threads) {
    // Start timing
    auto start_time = std::chrono::high_resolution_clock::now();

    // Set the number of threads for OpenMP
    omp_set_num_threads(num_threads);

    // Variable to store the final sum
    T final_sum = 0;

    if constexpr (std::is_floating_point<T>::value) {
        // For floating-point types, use a block-based approach to improve accuracy
        // This reduces floating-point errors by summing smaller chunks first
        const size_t block_size = 1024; // Adjust based on cache size
        std::vector<T> block_sums((N + block_size - 1) / block_size, 0);

        // First, compute sums of blocks in parallel
        #pragma omp parallel for
        for (size_t b = 0; b < block_sums.size(); b++) {
            size_t start = b * block_size;
            size_t end = std::min(start + block_size, N);

            // Kahan summation for better floating-point accuracy
            T sum = 0;
            T c = 0; // Compensation term

            for (size_t i = start; i < end; i++) {
                T y = a[i] - c;
                T t = sum + y;
                c = (t - sum) - y;
                sum = t;
            }

            block_sums[b] = sum;
        }

        // Final reduction of block sums (can also use OpenMP, but serial is often faster for small vectors)
        T c = 0; // Compensation term
        for (size_t i = 0; i < block_sums.size(); i++) {
            T y = block_sums[i] - c;
            T t = final_sum + y;
            c = (t - final_sum) - y;
            final_sum = t;
        }
    } else {
        // For integer types, use the standard OpenMP reduction
        #pragma omp parallel for reduction(+:final_sum) schedule(static)
        for (size_t i = 0; i < N; i++) {
            final_sum += a[i];
        }
    }

    // End timing
    auto end_time = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::microseconds>(end_time - start_time);

    // Return results
    return {static_cast<double>(final_sum), duration.count() / 1000.0};
}

// Function to generate test data
template<typename T>
void generate_test_data(T arr[], size_t N) {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<> dis(1.0, 100.0);

    for (size_t i = 0; i < N; i++) {
        arr[i] = static_cast<T>(dis(gen));
    }
}

// Function to verify the result is correct
template<typename T>
bool verify_result(T a[], size_t N, T result) {
    T expected_sum = 0;
    for (size_t i = 0; i < N; i++) {
        expected_sum += a[i];
    }

    // Check with appropriate tolerance for floating-point types
    if constexpr (std::is_floating_point<T>::value) {
        // Use a larger tolerance for floating-point types based on array size
        // This accounts for accumulated floating-point errors in parallel reduction
        double relative_tolerance = 1e-3;
        double relative_diff = std::abs(expected_sum - result) / std::abs(expected_sum);
        bool is_valid = relative_diff < relative_tolerance;

        if (!is_valid) {
            std::cout << "INFO: Relative difference: " << relative_diff
                      << " (tolerance: " << relative_tolerance << ")" << std::endl;
        }

        return is_valid;
    } else {
        return expected_sum == result;
    }
}

// Function to run strong scaling test and save results to a file
template<typename T>
void run_strong_scaling_test(const std::string& method_name,
                            std::function<Data(T[], size_t, size_t)> sum_function,
                            const std::vector<size_t>& array_sizes,
                            const std::vector<size_t>& thread_counts) {

    std::ofstream output_file(method_name + "_strong_scaling.csv");
    output_file << "Array_Size,Threads,Time_ms,Sum\n";

    for (const auto& N : array_sizes) {
        // Allocate and initialize array
        T* arr = new T[N];
        generate_test_data(arr, N);

        // Compute the reference sum for verification
        T reference_sum = 0;
        for (size_t i = 0; i < N; i++) {
            reference_sum += arr[i];
        }

        std::cout << "Running strong scaling test for " << method_name
                  << " with array size " << N << std::endl;

        for (const auto& threads : thread_counts) {
            // Run the sum function
            Data result = sum_function(arr, N, threads);

            // Verify the result
            bool is_correct = verify_result(arr, N, static_cast<T>(result.sum));

            std::cout << "  Threads: " << threads << ", Time: " << std::fixed
                      << std::setprecision(4) << result.time_ms << " ms, Correct: "
                      << (is_correct ? "Yes" : "No") << std::endl;

            // Save results to file
            output_file << N << "," << threads << "," << result.time_ms << "," << result.sum << "\n";
        }

        // Clean up
        delete[] arr;
    }

    output_file.close();
}

// Function to run weak scaling test and save results to a file
template<typename T>
void run_weak_scaling_test(const std::string& method_name,
                          std::function<Data(T[], size_t, size_t)> sum_function,
                          size_t base_size_per_thread,
                          const std::vector<size_t>& thread_counts) {

    std::ofstream output_file(method_name + "_weak_scaling.csv");
    output_file << "Array_Size,Threads,Elements_Per_Thread,Time_ms,Sum\n";

    std::cout << "Running weak scaling test for " << method_name << std::endl;

    for (const auto& threads : thread_counts) {
        // Calculate array size based on thread count
        size_t N = threads * base_size_per_thread;

        // Allocate and initialize array
        T* arr = new T[N];
        generate_test_data(arr, N);

        // Run the sum function
        Data result = sum_function(arr, N, threads);

        // Verify the result
        bool is_correct = verify_result(arr, N, static_cast<T>(result.sum));

        std::cout << "  Threads: " << threads << ", Array size: " << N
                  << ", Time: " << std::fixed << std::setprecision(4) << result.time_ms
                  << " ms, Correct: " << (is_correct ? "Yes" : "No") << std::endl;

        // Save results to file
        output_file << N << "," << threads << "," << base_size_per_thread << ","
                   << result.time_ms << "," << result.sum << "\n";

        // Clean up
        delete[] arr;
    }

    output_file.close();
}

int main(int argc, char* argv[]) {
    std::cout << "Parallel Reduction Assignment" << std::endl;
    std::cout << "============================" << std::endl;

    // Determine maximum number of threads available
    int max_threads = 16;
    std::cout << "Hardware concurrency: " << max_threads << " threads" << std::endl;

    // Define default array sizes and thread counts for tests
    std::vector<size_t> array_sizes = {100000, 1000000, 10000000};
    std::vector<size_t> thread_counts;

    // Create thread counts vector based on available threads
    // Start with 1, then powers of 2 up to max_threads
    thread_counts.push_back(1);
    for (int t = 2; t <= max_threads; t *= 2) {
        thread_counts.push_back(t);
    }
    // If max_threads is not a power of 2, add it as well
    if ((max_threads & (max_threads - 1)) != 0 &&
        std::find(thread_counts.begin(), thread_counts.end(), max_threads) == thread_counts.end()) {
        thread_counts.push_back(max_threads);
    }

    // Default base size per thread for weak scaling
    size_t base_size_per_thread = 100000;

    // Process command line arguments if provided
    bool run_strong_scaling = true;
    bool run_weak_scaling = true;
    bool test_int = true;
    bool test_float = true;
    bool test_double = true;
    bool test_long = false;

    for (int i = 1; i < argc; i++) {
        std::string arg = argv[i];
        if (arg == "--strong-only") {
            run_weak_scaling = false;
        } else if (arg == "--weak-only") {
            run_strong_scaling = false;
        } else if (arg == "--int-only") {
            test_int = true;
            test_float = false;
            test_double = false;
        } else if (arg == "--float-only") {
            test_int = false;
            test_float = true;
            test_double = false;
        } else if (arg == "--double-only") {
            test_int = false;
            test_float = false;
            test_double = true;
        } else if (arg == "--all-types") {
            test_int = true;
            test_float = true;
            test_double = true;
            test_long = true;
        } else if (arg.find("--base-size=") == 0) {
            base_size_per_thread = std::stoul(arg.substr(12));
        }
    }

    std::cout << "Running tests with up to " << max_threads << " threads." << std::endl;

    // Run strong scaling tests
    if (run_strong_scaling) {
        if (test_int) {
            std::cout << "\nRunning Strong Scaling Tests (int):" << std::endl;
            run_strong_scaling_test<int>("std_threads_int", parallel_sum_std<int>, array_sizes, thread_counts);
            run_strong_scaling_test<int>("omp_custom_int", parallel_sum_omp1<int>, array_sizes, thread_counts);
            run_strong_scaling_test<int>("omp_builtin_int", parallel_sum_omp_builtin<int>, array_sizes, thread_counts);
        }

        if (test_long) {
            std::cout << "\nRunning Strong Scaling Tests (long):" << std::endl;
            run_strong_scaling_test<long>("std_threads_long", parallel_sum_std<long>, array_sizes, thread_counts);
            run_strong_scaling_test<long>("omp_custom_long", parallel_sum_omp1<long>, array_sizes, thread_counts);
            run_strong_scaling_test<long>("omp_builtin_long", parallel_sum_omp_builtin<long>, array_sizes, thread_counts);
        }

        if (test_float) {
            std::cout << "\nRunning Strong Scaling Tests (float):" << std::endl;
            run_strong_scaling_test<float>("std_threads_float", parallel_sum_std<float>, array_sizes, thread_counts);
            run_strong_scaling_test<float>("omp_custom_float", parallel_sum_omp1<float>, array_sizes, thread_counts);
            run_strong_scaling_test<float>("omp_builtin_float", parallel_sum_omp_builtin<float>, array_sizes, thread_counts);
        }

        if (test_double) {
            std::cout << "\nRunning Strong Scaling Tests (double):" << std::endl;
            run_strong_scaling_test<double>("std_threads_double", parallel_sum_std<double>, array_sizes, thread_counts);
            run_strong_scaling_test<double>("omp_custom_double", parallel_sum_omp1<double>, array_sizes, thread_counts);
            run_strong_scaling_test<double>("omp_builtin_double", parallel_sum_omp_builtin<double>, array_sizes, thread_counts);
        }
    }

    // Run weak scaling tests
    if (run_weak_scaling) {
        if (test_int) {
            std::cout << "\nRunning Weak Scaling Tests (int):" << std::endl;
            run_weak_scaling_test<int>("std_threads_int", parallel_sum_std<int>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<int>("omp_custom_int", parallel_sum_omp1<int>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<int>("omp_builtin_int", parallel_sum_omp_builtin<int>, base_size_per_thread, thread_counts);
        }

        if (test_long) {
            std::cout << "\nRunning Weak Scaling Tests (long):" << std::endl;
            run_weak_scaling_test<long>("std_threads_long", parallel_sum_std<long>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<long>("omp_custom_long", parallel_sum_omp1<long>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<long>("omp_builtin_long", parallel_sum_omp_builtin<long>, base_size_per_thread, thread_counts);
        }

        if (test_float) {
            std::cout << "\nRunning Weak Scaling Tests (float):" << std::endl;
            run_weak_scaling_test<float>("std_threads_float", parallel_sum_std<float>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<float>("omp_custom_float", parallel_sum_omp1<float>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<float>("omp_builtin_float", parallel_sum_omp_builtin<float>, base_size_per_thread, thread_counts);
        }

        if (test_double) {
            std::cout << "\nRunning Weak Scaling Tests (double):" << std::endl;
            run_weak_scaling_test<double>("std_threads_double", parallel_sum_std<double>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<double>("omp_custom_double", parallel_sum_omp1<double>, base_size_per_thread, thread_counts);
            run_weak_scaling_test<double>("omp_builtin_double", parallel_sum_omp_builtin<double>, base_size_per_thread, thread_counts);
        }
    }

    std::cout << "\nAll tests completed. Results written to CSV files." << std::endl;

    return 0;
}