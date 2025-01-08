// Registered Users

document.addEventListener("DOMContentLoaded", () => {
    // Product Management
    const productForm = document.getElementById("product-form");
    const productList = document.getElementById("product-list");

    productForm.addEventListener("submit", (e) => {
        e.preventDefault();
    
        const name = document.getElementById("product-name").value;
        const price = document.getElementById("product-price").value;
        const image = document.getElementById("product-image").files[0];
    
        const productItem = document.createElement("li");
        const img = document.createElement("img");
        img.src = URL.createObjectURL(image);
        img.alt = `${name} image`;
        img.style.width = "50px";
        img.style.height = "50px";
    
        productItem.textContent = `${name} - $${price}`;
        productItem.appendChild(img);
    
        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";
        deleteButton.addEventListener("click", () => productItem.remove());
    
        productItem.appendChild(deleteButton);
        productList.appendChild(productItem);
    
        productForm.reset();
    });
});
    

    // User Management
    document.addEventListener("DOMContentLoaded", () => {
        const userForm = document.getElementById("user-form");
        const registrationRequests = document.getElementById("registrationRequests");
        const qrCodesContainer = document.getElementById("qrCodes"); // Ensure this exists in the HTML
    
        let userIdCounter = 1;
    
        userForm.addEventListener("submit", (e) => {
            e.preventDefault();
    
            const name = document.getElementById("user-name").value;
            const lastName = document.getElementById("user-last-name").value;
            const age = document.getElementById("user-age").value;
            const role = document.getElementById("user-role").value;
            const picture = document.getElementById("user-picture").files[0];
    
            const id = userIdCounter++;
    
            const requestItem = document.createElement("li");
    
            const img = document.createElement("img");
            img.src = URL.createObjectURL(picture);
            img.alt = `${name}'s profile picture`;
            img.style.width = "50px";
            img.style.height = "50px";
            img.style.borderRadius = "50%";
    
            const userInfo = document.createElement("span");
            userInfo.textContent = `${name} ${lastName} - Role: ${role}, Age: ${age}, ID: ${id}`;
            userInfo.style.marginLeft = "10px";
    
            requestItem.appendChild(img);
            requestItem.appendChild(userInfo);
    
            // Approve button
            const approveButton = document.createElement("button");
            approveButton.textContent = "Approve";
            approveButton.style.marginLeft = "10px";
            approveButton.addEventListener("click", () => {
                alert(`${name} ${lastName} approved!`);
                requestItem.remove();
    
                // Generate and display QR code
                const qrCodeDiv = document.createElement("div");
                const qrCodeContainer = document.createElement("div");
                qrCodeContainer.style.border = "1px solid #ddd";
                qrCodeContainer.style.padding = "10px";
                qrCodeContainer.style.margin = "10px";
    
                const qrCodeTitle = document.createElement("p");
                qrCodeTitle.textContent = `QR Code for ${id}`;
                qrCodeContainer.appendChild(qrCodeTitle);
    
                qrCodeContainer.appendChild(qrCodeDiv);
                qrCodesContainer.appendChild(qrCodeContainer);
    
                new QRCode(qrCodeDiv, {
                    text: `https://example.com/user?id=${id}`,
                    width: 128,
                    height: 128,
                });
            });
    
            // Reject button
            const rejectButton = document.createElement("button");
            rejectButton.textContent = "Reject";
            rejectButton.style.marginLeft = "10px";
            rejectButton.addEventListener("click", () => requestItem.remove());
    
            requestItem.appendChild(approveButton);
            requestItem.appendChild(rejectButton);
            registrationRequests.appendChild(requestItem);
    
            // Reset form
            userForm.reset();
        });
    });
    

// Registration Management
document.addEventListener("DOMContentLoaded", () => {
    const mockRequests = ["John Doe", "Jane Smith"];
    const registrationRequests = document.getElementById("registrationRequests");
    const qrCodesContainer = document.getElementById("qrCodes");

    const addRequest = (name) => {
        const requestItem = document.createElement("li");
        requestItem.textContent = name;

        const approveButton = document.createElement("button");
        approveButton.textContent = "Approve";
        approveButton.addEventListener("click", () => {
            alert(`${name} approved!`);
            requestItem.remove();

            const qrCodeDiv = document.createElement("div");
            const qrCodeContainer = document.createElement("div");
            qrCodeContainer.classList.add("qr-code-container");
            qrCodeContainer.style.border = "1px solid #ddd";
            qrCodeContainer.style.padding = "10px";
            qrCodeContainer.style.margin = "10px";

            const qrCodeTitle = document.createElement("p");
            qrCodeTitle.textContent = `QR Code for ${name}`;
            qrCodeContainer.appendChild(qrCodeTitle);

            qrCodeContainer.appendChild(qrCodeDiv);
            qrCodesContainer.appendChild(qrCodeContainer);

            new QRCode(qrCodeDiv, {
                text: `https://example.com/user?name=${encodeURIComponent(name)}`,
                width: 128,
                height: 128,
            });
        });

        const rejectButton = document.createElement("button");
        rejectButton.textContent = "Reject";
        rejectButton.addEventListener("click", () => requestItem.remove());

        requestItem.appendChild(approveButton);
        requestItem.appendChild(rejectButton);
        registrationRequests.appendChild(requestItem);
    };

    // Populate mock requests
    mockRequests.forEach(addRequest);
});
