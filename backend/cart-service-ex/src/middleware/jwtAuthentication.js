// middleware/jwtAuthentication.js
const jwt = require('jsonwebtoken');

// Lấy secret key từ environment
const signerKey = process.env.JWT_SIGNING_KEY;

const jwtAuthentication = (req, res, next) => {
    const authHeader = req.headers['authorization'];

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({message: 'Missing or invalid Authorization header'});
    }

    const token = authHeader.substring(7); // Bỏ "Bearer "

    try {
        const claims = jwt.verify(token, signerKey); // validate và decode token

        // Lấy userId và roles từ payload
        req.user = {
            userId: claims.sub,
            roles: Array.isArray(claims.role) ? claims.role : []
        };

        next(); // tiếp tục xử lý request
    } catch (err) {
        console.error('Invalid JWT:', err.message);
        return res.status(401).json({message: 'Invalid token'});
    }
};

module.exports = jwtAuthentication;