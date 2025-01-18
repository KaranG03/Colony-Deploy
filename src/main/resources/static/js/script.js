 // Show Signup Form
function showSignUpForm() {
    document.getElementById('login-form').classList.add('hidden');
    document.getElementById('signup-form').classList.remove('hidden');
}

// Show Login Form
function showLoginForm() {
    document.getElementById('signup-form').classList.add('hidden');
    document.getElementById('login-form').classList.remove('hidden');
}

// Sign Up Functionality
async function signUp() {
    const username = document.getElementById('signup-username').value;
    const password = document.getElementById('signup-password').value;
    const email = document.getElementById('signup-email').value;

    const data = { username, password, mail: email };

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/public/sign-up', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            alert('Signup successful! Please login.');
            showLoginForm();
        } else {
            const error = await response.text();
            alert(`Signup failed: ${error}`);
        }
    } catch (error) {
        console.error('Signup error:', error);
    }
}

// Login Functionality
async function login() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value.trim();

    if (!username || !password) {
        alert('Please enter both username and password.');
        return;
    }

    const data = { username, password };

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/public/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            const jwt = await response.text(); // Backend returns the JWT token
            alert('Login successful!');
            localStorage.setItem('token', jwt); // Save JWT token to localStorage

            // Hide login form and show buttons
            document.getElementById('login-form').classList.add('hidden');
            document.getElementById('logout-button').classList.remove('hidden');
            document.getElementById('add-colony-button').classList.remove('hidden');
            document.getElementById('view-customers-button').classList.remove('hidden');

            // Fetch user profile to check prime status
            const userResponse = await fetch('https://colony-deploy-production.up.railway.app/user/prime-status', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${jwt}`,
                },
            });

            if (userResponse.ok) {
                const userData = await userResponse.json();
                const primeSubscriptionSection = document.getElementById('prime-subscription');
                const primeStatusDiv = document.getElementById('prime-status');
                const primePurchaseDiv = document.getElementById('prime-purchase');

                if (userData.primeStatus === 1) {
                    // User is a prime member
                    primeStatusDiv.classList.remove('hidden');
                    primePurchaseDiv.classList.add('hidden');
                } else {
                    // User is not a prime member
                    primeStatusDiv.classList.add('hidden');
                    primePurchaseDiv.classList.remove('hidden');
                }

                primeSubscriptionSection.classList.remove('hidden');
            }

            // Load colonies
            fetchColonies();
        } else {
            const error = await response.json();
            alert(`Login failed: ${error.message || 'Unknown error'}`);
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. Please try again.');
    }
}




// Initialize Payment
async function initiatePayment() {
    const token = localStorage.getItem('token'); // Retrieve JWT token

    if (!token) {
        alert("Please log in to make a payment.");
        return;
    }

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/api/payments/create-order?amount=1&currency=INR', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || "Failed to create order");
        }

        const order = await response.json();
        const options = {
            key: "rzp_live_Uci7M1MhZUBOga", // Replace with your Razorpay test key
            amount: order.amount, // Amount in paise
            currency: order.currency,
            name: "Prime Subscription",
            description: "Subscribe to Prime Membership",
            order_id: order.id,
            handler: function (response) {
                alert("Payment Successful! Payment ID: " + response.razorpay_payment_id);
                // Optionally update UI to show Prime Subscription status
                updatePrimeStatus();
            },
            theme: {
                color: "#3399cc"
            }
        };

        const rzp = new Razorpay(options);
        rzp.open();
    } catch (error) {
        console.error("Error:", error.message);
        alert("Failed to create an order. Please try again.");
    }
}

// Update UI for Prime Subscription
function updatePrimeStatus() {
    const primeSection = document.getElementById('prime-subscription');
    if (primeSection) {
        primeSection.innerHTML = "<h2>Thank you for subscribing to Prime Membership!</h2>";
    }
}


function addNewColonyButton() {
    // Find the colonies container
    const coloniesContainer = document.getElementById('colonies-container');

    // Check if the button already exists
    if (document.getElementById('add-colony-button')) return;

    // Create the button element
    const button = document.createElement('button');
    button.id = 'add-colony-button';
    button.textContent = 'Add New Colony';
    button.onclick = showNewColonyForm;

    // Insert the button at the top of the colonies container
    coloniesContainer.insertAdjacentElement('beforebegin', button);
}

async function fetchColonies() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/user', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const colonies = await response.json();
            displayColonies(colonies);

            // Show the colonies container
            document.getElementById('colonies-container').classList.remove('hidden');

            // Add the "Add New Colony" button
            addNewColonyButton();
        } else {
            const error = await response.text();
            alert(`Failed to fetch colonies: ${error}`);
        }
    } catch (error) {
        console.error('Error fetching colonies:', error);
    }
}

// Display Colonies
function displayColonies(colonies) {
    const coloniesList = document.getElementById('colonies-list');
    coloniesList.innerHTML = '';

    colonies.forEach(colony => {
        const li = document.createElement('li');
        li.textContent = colony.name;
        li.onclick = () => fetchColonyDetails(colony.id);
        coloniesList.appendChild(li);
    });
}

// Fetch colony details by ID
async function fetchColonyDetails(colonyId) {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/user', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const colonies = await response.json();
            const colony = colonies.find(c => c.id === colonyId);

            if (colony) {
                displayColonyDetails(colony);
            } else {
                alert('Colony not found');
            }
        } else {
            const error = await response.text();
            alert(`Failed to fetch colony details: ${error}`);
        }
    } catch (error) {
        console.error('Error fetching colony details:', error);
    }
}

// Display colony details and plots
function displayColonyDetails(colony) {
    document.getElementById('colony-name').textContent = colony.name;
    document.getElementById('colony-location').textContent = colony.location || 'Unknown';
    document.getElementById('colony-partners').textContent = colony.partners.join(', ') || 'Unknown';
    document.getElementById('colony-numPlots').textContent = colony.numPlots || colony.plots.length;
    document.getElementById('colony-details').classList.remove('hidden');

    displayColonyPlots(colony.plots);
}

// Display plots for a colony
function displayColonyPlots(plots) {
    const plotsGrid = document.getElementById('plots-grid');
    plotsGrid.innerHTML = '';

    plots.forEach(plot => {
        const plotBox = document.createElement('div');
        // Apply class based on plot status
        plotBox.classList.add('plot-box', plot.sold ? 'sold' : 'available');
        plotBox.innerHTML = `
            <p><strong>Plot No:</strong> ${plot.plotNo}</p>
            <p><strong>Sold:</strong> ${plot.sold ? 'Yes' : 'No'}</p>
            <p><strong>Owner:</strong> ${plot.owner || 'N/A'}</p>
            <p><strong>Price:</strong> ${plot.price || 'N/A'}</p>
        `;

        const editButton = document.createElement('button');
        editButton.textContent = 'Edit';
        editButton.onclick = () => openModal(plot);
        plotBox.appendChild(editButton);

        plotsGrid.appendChild(plotBox);
    });

    document.getElementById('plots-container').classList.remove('hidden');
}

let selectedPlotId = null;

// Open modal to edit plot details
function openModal(plot) {
    selectedPlotId = plot.id || null;

    // Safely set values only if elements exist
    const plotNoField = document.getElementById('plotNo');
    const priceField = document.getElementById('price');
    const ownerField = document.getElementById('owner');
    const soldField = document.getElementById('sold');
    const regToField = document.getElementById('regTo');
    const custAadharField = document.getElementById('custAadhar');
    const custPhoneNumField = document.getElementById('custPhoneNum');
    const custAddField = document.getElementById('custAdd');
    const noteField = document.getElementById('note');

    if (plotNoField) plotNoField.value = plot.plotNo || '';
    if (priceField) priceField.value = plot.price || '';
    if (ownerField) ownerField.value = plot.owner || '';
    if (soldField) soldField.value = plot.sold ? 'true' : 'false';
    if (regToField) regToField.value = plot.regTo || '';
    if (custAadharField) custAadharField.value = plot.custAadhar || '';
    if (custPhoneNumField) custPhoneNumField.value = plot.custPhoneNum || '';
    if (custAddField) custAddField.value = plot.custAdd || '';
    if (noteField) noteField.value = plot.note || '';

    // Show the modal
    document.getElementById('plot-edit-modal').classList.remove('hidden');
}
async function savePlotDetails() {
    // Safely retrieve input elements
    const plotNoField = document.getElementById('plotNo');
    const priceField = document.getElementById('price');
    const ownerField = document.getElementById('owner');
    const soldField = document.getElementById('sold');
    const regToField = document.getElementById('regTo');
    const custAadharField = document.getElementById('custAadhar');
    const custPhoneNumField = document.getElementById('custPhoneNum');
    const custAddField = document.getElementById('custAdd');
    const noteField = document.getElementById('note');

    // Build the plot details object
    const plotDetails = {
        plotNo: plotNoField ? plotNoField.value : '',
        price: priceField ? priceField.value : '',
        owner: ownerField ? ownerField.value : '',
        sold: soldField ? soldField.value === 'true' : false,
        regTo: regToField ? regToField.value : '',
        custAadhar: custAadharField ? custAadharField.value : '',
        custPhoneNum: custPhoneNumField ? custPhoneNumField.value : '',
        custAdd: custAddField ? custAddField.value : '',
        note: noteField ? noteField.value : '',
    };

    try {
        const response = await fetch(`https://colony-deploy-production.up.railway.app/plot/edit-plot/${selectedPlotId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify(plotDetails),
        });

        if (response.ok) {
            alert('Plot details updated successfully!');
            closeModal();
            fetchColonies(); // Refresh colonies
        } else {
            const error = await response.text();
            alert(`Failed to update plot: ${error}`);
        }
    } catch (error) {
        console.error('Error saving plot details:', error);
    }
}


// Close modal
function closeModal() {
    document.getElementById('plot-edit-modal').classList.add('hidden');
}



function showNewColonyForm() {
    document.getElementById('new-colony-container').classList.remove('hidden');
}

// Hide the Add Colony Form
function hideNewColonyForm() {
    document.getElementById('new-colony-container').classList.add('hidden');
    // Optionally clear form inputs
    document.getElementById('new-colony-form').reset();
}

// Add Colony Functionality
async function createNewColony() {
    const name = document.getElementById('new-colony-name').value;
    const location = document.getElementById('new-colony-location').value;
    const partners = document.getElementById('new-colony-partners').value.split(',').map(p => p.trim());
    const numPlots = parseInt(document.getElementById('new-colony-numPlots').value, 10);

    const data = { name, location, partners, numPlots };

    try {
        const token = localStorage.getItem('token');

        const response = await fetch('https://colony-deploy-production.up.railway.app/user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            alert('Colony added successfully!');
            fetchColonies(); // Refresh the colonies list
            hideNewColonyForm();
        } else {
            const error = await response.text();
            alert(`Failed to add colony: ${error}`);
        }
    } catch (error) {
        console.error('Error adding colony:', error);
    }
}

// Show "View All Customers" Button
function showCustomerButton() {
    const buttonContainer = document.createElement('div');
    buttonContainer.style.position = 'absolute';
    buttonContainer.style.top = '10px';
    buttonContainer.style.right = '10px';

    const customerButton = document.createElement('button');
    customerButton.textContent = 'View All Customers';
    customerButton.onclick = fetchAllCustomers;

    buttonContainer.appendChild(customerButton);
    document.body.appendChild(buttonContainer);
}

// Fetch and Display All Customers
async function fetchAllCustomers() {
    const token = localStorage.getItem('token');

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/user/all-cust', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const customers = await response.json();
            displayCustomers(customers);
        } else {
            const error = await response.text();
            alert(`Failed to fetch customers: ${error}`);
        }
    } catch (error) {
        console.error('Error fetching customers:', error);
    }
}

// Display Customers in a Modal
function displayCustomers(customers) {
    const modalHtml = `
        <div class="plot-edit-modal" id="customer-modal">
            <div class="modal-content">
                <h2>All Customers</h2>
                <ul>
                    ${customers.map(c => `
                        <li>
                            <strong>Name:</strong> ${c.custName || 'N/A'}<br>
                            <strong>Phone:</strong> ${c.phNum || 'N/A'}<br>
                            <strong>Address:</strong> ${c.address || 'N/A'}<br>
                            <strong>Aadhar:</strong> ${c.aadharNum || 'N/A'}<br>
                            <strong>Plots:</strong>
                            <ul>
                                ${c.custPlots.map(plot => `
                                    <li>
                                        Plot No: ${plot.plotNo}, Colony: ${plot.colonyName}, Price: ${plot.price}
                                    </li>`).join('')}
                            </ul>
                        </li>
                    `).join('')}
                </ul>
                <button type="button" onclick="closeCustomerModal()">Close</button>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', modalHtml);
}

// Close Customer Modal
function closeCustomerModal() {
    const modal = document.getElementById('customer-modal');
    if (modal) modal.remove();
}

function logout() {
    // Remove JWT token
    localStorage.removeItem('token');

    // Hide all UI components
    document.getElementById('logout-button').classList.add('hidden');
    document.getElementById('add-colony-button').classList.add('hidden');
    document.getElementById('view-customers-button').classList.add('hidden');
    document.getElementById('colonies-container').classList.add('hidden');
    document.getElementById('colony-details').classList.add('hidden');
    document.getElementById('plots-container').classList.add('hidden');
    document.getElementById('prime-subscription').classList.add('hidden');

    // Hide new colony form if visible
    const newColonyContainer = document.getElementById('new-colony-container');
    if (!newColonyContainer.classList.contains('hidden')) {
        newColonyContainer.classList.add('hidden');
    }

    // Reset UI to login state
    document.getElementById('login-form').classList.remove('hidden');
}


// Show Forgot Password Form
function showForgotPasswordForm() {
    document.getElementById('login-form').classList.add('hidden');
    document.getElementById('forgot-password-form').classList.remove('hidden');
}

// Send Forgot Password Request
async function sendForgotPasswordRequest() {
    const username = document.getElementById('forgot-username').value;

    if (!username) {
        alert('Please enter your username.');
        return;
    }

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/public/forgot-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username }),
        });

        if (response.ok) {
            alert('OTP sent to your registered email.');
            document.getElementById('forgot-password-form').classList.add('hidden');
            document.getElementById('otp-verification-form').classList.remove('hidden');
            document.getElementById('otp-username').value = username; // Prefill username
        } else {
            alert('Failed to send OTP. Please check your username.');
        }
    } catch (error) {
        console.error('Error sending forgot password request:', error);
        alert('An error occurred. Please try again.');
    }
}

// Verify OTP
async function verifyOTP() {
    const username = document.getElementById('otp-username').value;
    const otp = document.getElementById('otp').value;

    if (!username || !otp) {
        alert('Please enter both username and OTP.');
        return;
    }

    try {
        const response = await fetch('https://colony-deploy-production.up.railway.app/verification', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, otp }),
        });

        if (response.ok) {
            alert('OTP verified successfully. You can now reset your password.');
            document.getElementById('otp-verification-form').classList.add('hidden');
            document.getElementById('reset-password-form').classList.remove('hidden');
            document.getElementById('reset-username').value = username; // Prefill username
        } else {
            alert('Invalid or expired OTP. Please try again.');
        }
    } catch (error) {
        console.error('Error verifying OTP:', error);
        alert('An error occurred. Please try again.');
    }
}

// Reset Password
async function resetPassword() {
    const username = document.getElementById('reset-username').value;
    const newPassword = document.getElementById('new-password').value;

    if (!username || !newPassword) {
        alert('Please enter both username and new password.');
        return;
    }

    try {
        const response = await fetch(`https://colony-deploy-production.up.railway.app/user/reset-password?username=${username}&newP=${newPassword}`, {
            method: 'POST',
        });

        if (response.ok) {
            alert('Password reset successfully! Please log in with your new password.');
            document.getElementById('reset-password-form').classList.add('hidden');
            document.getElementById('login-form').classList.remove('hidden');
        } else {
            alert('Failed to reset password. Please try again.');
        }
    } catch (error) {
        console.error('Error resetting password:', error);
        alert('An error occurred. Please try again.');
    }
}



function showLoginFormFromForgotPassword() {
    // Hide the Forgot Password form
    document.getElementById('forgot-password-form').classList.add('hidden');

    // Hide other forms, just in case they are visible
    document.getElementById('otp-verification-form').classList.add('hidden');
    document.getElementById('reset-password-form').classList.add('hidden');
    document.getElementById('signup-form').classList.add('hidden');

    // Show the Login form
    document.getElementById('login-form').classList.remove('hidden');
}
